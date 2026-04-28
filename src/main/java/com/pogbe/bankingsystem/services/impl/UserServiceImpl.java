package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.requests.UserLoginRequest;
import com.pogbe.bankingsystem.dto.responses.GenericSuccessResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserLoginResponse;
import com.pogbe.bankingsystem.exceptions.custom.FileHandlingException;
import com.pogbe.bankingsystem.exceptions.custom.ResourceNotAvailable;
import com.pogbe.bankingsystem.mappers.UserMapper;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.AesEncryptionService;
import com.pogbe.bankingsystem.services.interfaces.UserService;
import com.pogbe.bankingsystem.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserModelRepository userModelRepository;
    private final AesEncryptionService aesEncryptionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserModelRepository userModelRepository, AesEncryptionService aesEncryptionService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userModelRepository = userModelRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public SuccessUserCreatedResponse createUser(UserCreateRequest userCreateRequest) {
        // some simple validation
        if (!properUsernameFormat(userCreateRequest.getUsername())) {
            throw new IllegalArgumentException("Username must be at least 3 characters long and can't contain only numbers");
        }
        if (userCreateRequest.getAccountPin().length() != 4 || !NumericUtils.isNumeric(userCreateRequest.getAccountPin())) {
            throw new IllegalArgumentException("Account pin must be only 4 digits");
        }
        if (userCreateRequest.getPhoneNumber().length() < 10 || userCreateRequest.getPhoneNumber().length() > 12) {
            throw new IllegalArgumentException("Phone number must be between 10 and 12 digits long");
        }
        if (!PasswordValidatorUtils.isValidPassword(userCreateRequest.getPassword())) {
            throw new IllegalArgumentException(PasswordValidatorUtils.getInvalidPasswordMessage());
        }

        // checking if a username or phone number already exists
        if (userModelRepository.existsByUsername(userCreateRequest.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        if (userModelRepository.existsByPhoneNumber(userCreateRequest.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        String generatedAccountNumber = AccountNumberGenerator.generateAccountNumber(); // generate a unique account number
        String accountPin = userCreateRequest.getAccountPin();

        String firstThreeDigits = generatedAccountNumber.substring(0, 3);
        String lastThreeDigits = generatedAccountNumber.substring(generatedAccountNumber.length() - 3);
        // encrypting sensitive fields
        String encryptedAccountPin = aesEncryptionService.encrypt(accountPin);
        String encryptedAccountNumber = aesEncryptionService.encrypt(generatedAccountNumber);

        Account account = new Account(encryptedAccountNumber, firstThreeDigits, lastThreeDigits, encryptedAccountPin);
        account.setAccountBalance(BigDecimal.valueOf(10000)); // some initial balance
        UserModel savedUser = UserMapper.mapRequestDtoToUserModel(userCreateRequest);
        account.setUser(savedUser);

        savedUser.setUserAccount(account);
        savedUser.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        savedUser = userModelRepository.save(savedUser);

        return new SuccessUserCreatedResponse(savedUser.getUsername(), generatedAccountNumber);
    }

    @Override
    public SuccessUserLoginResponse loginUser(UserLoginRequest userLoginRequest) {
        // some basic validation
        if (!properUsernameFormat(userLoginRequest.getUsername())) {
            throw new IllegalArgumentException("Username must be at least 3 characters long and can't contain only numbers");
        }
        if (!PasswordValidatorUtils.isValidPassword(userLoginRequest.getPassword())) {
            throw new IllegalArgumentException(PasswordValidatorUtils.getInvalidPasswordMessage());
        }

        Optional<UserModel> userModel = userModelRepository.findByUsername(userLoginRequest.getUsername());
        if (userModel.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        UserModel gottenUser = userModel.get();
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), gottenUser.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", gottenUser.getUsername());
        claims.put("userId", gottenUser.getId());
        claims.put("accountId",gottenUser.getUserAccount().getId());
        claims.put("role", "USER");
        String token = jwtUtils.generateAccessToken(gottenUser.getUsername(), claims);
        SuccessUserLoginResponse successUserLoginResponse = new SuccessUserLoginResponse();
        successUserLoginResponse.setAccessToken(token);
        successUserLoginResponse.setUsername(gottenUser.getUsername());
        BigDecimal balance = gottenUser.getUserAccount().getAccountBalance();
        successUserLoginResponse.setAccountBalance(balance);
        return successUserLoginResponse;
    }

    @Override
    public GenericSuccessResponse updateProfilePicture(Authentication authentication, MultipartFile file) {
        if (FilesValidatorUtils.isValidFile(file, 1024*1024*10L, "image/")) {
            throw new IllegalArgumentException("Invalid file type. File must be an image and less than 10MB.");
        }
        UserModel user = getUserFromAuthentication(authentication);
        try {
            user.setProfilePicture(ImageUtils.compressImage(file.getBytes()));
            user.setProfilePictureContentType(file.getContentType());
            userModelRepository.save(user);
            return new GenericSuccessResponse("Profile picture updated successfully");
        } catch (IOException e) {
            throw new FileHandlingException("Error processing file");
        }
    }

    @Override
    public byte[] getProfilePicture(Authentication authentication) {
        UserModel user = getUserFromAuthentication(authentication);
        byte[] imageData = user.getProfilePicture();
        if (imageData == null) {
            throw new ResourceNotAvailable("Profile picture not set");
        }
        return ImageUtils.decompressImage(imageData);
    }

    @Override
    public String getProfilePictureContentType(Authentication authentication) {
        UserModel user = getUserFromAuthentication(authentication);
        return user.getProfilePictureContentType() == null ? "image/jpeg" : user.getProfilePictureContentType();
    }

    @Override
    public GenericSuccessResponse deleteProfilePicture(Authentication authentication) {
        UserModel user = getUserFromAuthentication(authentication);
        user.setProfilePicture(null);
        user.setProfilePictureContentType(null);
        userModelRepository.save(user);
        return new GenericSuccessResponse("Profile picture deleted successfully");
    }

    private boolean properUsernameFormat(String username) {
        return username.length() >= 3 && !NumericUtils.isNumeric(username);
    }

    private UserModel getUserFromAuthentication(Authentication authentication) {
        return userModelRepository.findByUsername(authentication.getName()).orElseThrow(()->new IllegalArgumentException("Authentication failed: Invalid username or password"));
    }
}

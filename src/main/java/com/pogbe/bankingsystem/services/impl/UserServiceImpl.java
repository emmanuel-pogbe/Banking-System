package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.requests.UserLoginRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserLoginResponse;
import com.pogbe.bankingsystem.mappers.UserMapper;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.AesEncryptionService;
import com.pogbe.bankingsystem.services.interfaces.UserService;
import com.pogbe.bankingsystem.utils.AccountNumberGenerator;
import com.pogbe.bankingsystem.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserModelRepository userModelRepository, AesEncryptionService aesEncryptionService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userModelRepository = userModelRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    @Transactional
    public SuccessUserCreatedResponse createUser(UserCreateRequest userCreateRequest) {
        if (userModelRepository.existsByUsername(userCreateRequest.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        if (userModelRepository.existsByPhoneNumber(userCreateRequest.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if (userCreateRequest.getAccountPin().length() != 4) {
            throw new DataIntegrityViolationException("Account pin must be 4 digits");
        }
        
        String generatedAccountNumber = AccountNumberGenerator.generateAccountNumber();
        String accountPin = userCreateRequest.getAccountPin();

        String firstThreeDigits = generatedAccountNumber.substring(0, 3);
        String lastThreeDigits = generatedAccountNumber.substring(generatedAccountNumber.length() - 3);
        // encrypting sensitive fields
        String encryptedAccountPin = aesEncryptionService.encrypt(accountPin);
        String encryptedAccountNumber = aesEncryptionService.encrypt(generatedAccountNumber);

        Account account = new Account(encryptedAccountNumber, firstThreeDigits, lastThreeDigits, encryptedAccountPin);

        UserModel savedUser = UserMapper.mapRequestDtoToUserModel(userCreateRequest);
        account.setUser(savedUser);

        savedUser.setUserAccount(account);
        savedUser.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        savedUser = userModelRepository.save(savedUser);

        return new SuccessUserCreatedResponse(savedUser.getUsername(), generatedAccountNumber);
    }

    @Override
    public SuccessUserLoginResponse loginUser(UserLoginRequest userLoginRequest) {
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
        String token = jwtUtils.generateAccessToken(gottenUser.getUsername(), claims);
        SuccessUserLoginResponse successUserLoginResponse = new SuccessUserLoginResponse();
        successUserLoginResponse.setAccessToken(token);
        successUserLoginResponse.setUsername(gottenUser.getUsername());
        BigDecimal balance = gottenUser.getUserAccount().getAccountBalance();
        successUserLoginResponse.setAccountBalance(balance);
        return successUserLoginResponse;
    }
}

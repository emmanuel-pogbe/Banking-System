package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.mappers.UserMapper;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.AesEncryptionService;
import com.pogbe.bankingsystem.services.interfaces.UserCreationService;
import com.pogbe.bankingsystem.utils.AccountNumberGenerator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserCreationServiceImpl implements UserCreationService {
    private final UserModelRepository userModelRepository;
    private final AesEncryptionService aesEncryptionService;
    private final PasswordEncoder passwordEncoder;

    public UserCreationServiceImpl(UserModelRepository userModelRepository, AesEncryptionService aesEncryptionService, PasswordEncoder passwordEncoder) {
        this.userModelRepository = userModelRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public SuccessUserCreatedResponse createUser(UserCreateRequest userCreateRequest) {
        Optional<UserModel> usernameOptional = userModelRepository.findByUsername(userCreateRequest.getUsername());
        if (usernameOptional.isPresent()) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        Optional<UserModel> phoneNumberOptional = userModelRepository.findByPhoneNumber(userCreateRequest.getPhoneNumber());
        if (phoneNumberOptional.isPresent()) {
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
}

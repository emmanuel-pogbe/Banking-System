package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.mappers.UserMapper;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.UserCreationService;
import com.pogbe.bankingsystem.utils.AccountNumberGenerator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserCreationServiceImpl implements UserCreationService {
    private UserModelRepository userModelRepository;

    public UserCreationServiceImpl(UserModelRepository userModelRepository) {
        this.userModelRepository = userModelRepository;
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

        // TODO: values to encrypt
        String generatedAccountNumber = AccountNumberGenerator.generateAccountNumber();
        String accountPin = userCreateRequest.getAccountPin();

        String firstThreeDigits = generatedAccountNumber.substring(0, 3);
        String lastThreeDigits = generatedAccountNumber.substring(generatedAccountNumber.length() - 3);
        Account account = new Account(generatedAccountNumber, firstThreeDigits, lastThreeDigits, accountPin);
        UserModel savedUser = UserMapper.mapRequestDtoToUserModel(userCreateRequest);

        savedUser.setUserAccount(account);

        savedUser = userModelRepository.save(savedUser);
        return null;
    }
}

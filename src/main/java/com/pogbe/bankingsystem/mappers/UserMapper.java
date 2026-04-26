package com.pogbe.bankingsystem.mappers;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.models.UserModel;

import java.time.LocalDateTime;


// simple mapper to do stuff
public class UserMapper {
    public static UserModel mapRequestDtoToUserModel(UserCreateRequest userCreateRequest) {
        UserModel userModel = new UserModel();
        userModel.setUsername(userCreateRequest.getUsername());
        userModel.setPassword(userCreateRequest.getPassword());
        userModel.setPhoneNumber(userCreateRequest.getPhoneNumber());
        userModel.setFullName(userCreateRequest.getFullName());
        userModel.setCreatedAt(LocalDateTime.now());
        return userModel;
    }

    public static SuccessUserCreatedResponse mapUserModelToSuccessUserCreatedResponse(UserModel userModel) {
        SuccessUserCreatedResponse successUserCreatedResponse = new SuccessUserCreatedResponse();
        successUserCreatedResponse.setUsername(userModel.getUsername());
        successUserCreatedResponse.setAccountNumber(userModel.getUserAccount().getAccountNumber());
        return successUserCreatedResponse;
    }
}

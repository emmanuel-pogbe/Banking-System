package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.requests.UserLoginRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserLoginResponse;

public interface UserService {
    SuccessUserCreatedResponse createUser(UserCreateRequest userCreateRequest);

    SuccessUserLoginResponse loginUser(UserLoginRequest userLoginRequest);
}

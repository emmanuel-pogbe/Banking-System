package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;

public interface UserCreationService {
    SuccessUserCreatedResponse createUser(UserCreateRequest userCreateRequest);
}

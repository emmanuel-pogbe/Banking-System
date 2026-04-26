package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.requests.UserLoginRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserLoginResponse;
import com.pogbe.bankingsystem.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessUserLoginResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.ok(userService.loginUser(userLoginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessUserCreatedResponse> registerUser(@RequestBody UserCreateRequest userCreateRequest) {
        return ResponseEntity.ok(userService.createUser(userCreateRequest));
    }
}

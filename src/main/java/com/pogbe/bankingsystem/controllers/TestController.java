package com.pogbe.bankingsystem.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.services.interfaces.UserCreationService;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserCreationService userCreationService;

    public TestController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @PostMapping("/api/v1/create")
    public ResponseEntity<SuccessUserCreatedResponse> test(@RequestBody UserCreateRequest userCreateRequest) {
        return ResponseEntity.ok(userCreationService.createUser(userCreateRequest));
    }
    
}

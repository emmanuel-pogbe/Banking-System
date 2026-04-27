package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.dto.requests.TransferMoneyRequest;
import com.pogbe.bankingsystem.dto.requests.UserAccountNumberRequest;
import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.requests.UserLoginRequest;
import com.pogbe.bankingsystem.dto.responses.SuccessTransfer;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserLoginResponse;
import com.pogbe.bankingsystem.dto.responses.UserAccountInformation;
import com.pogbe.bankingsystem.services.impl.TransactionServiceImpl;
import com.pogbe.bankingsystem.services.interfaces.TransactionService;
import com.pogbe.bankingsystem.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final TransactionService transactionService;

    public UserController(UserService userService, TransactionServiceImpl transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Operation(summary = "Login user", description = "Login user with username and password")
    @PostMapping("/login")
    public ResponseEntity<SuccessUserLoginResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.ok(userService.loginUser(userLoginRequest));
    }

    @Operation(summary = "Register user", description = "Create a new user account")
    @PostMapping("/register")
    public ResponseEntity<SuccessUserCreatedResponse> registerUser(@RequestBody UserCreateRequest userCreateRequest) {
        return ResponseEntity.ok(userService.createUser(userCreateRequest));
    }

    @Operation(summary = "Get my balance", description = "Get balance based on the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getAccountBalance(authentication));
    }

    @Operation(summary = "Get my account number", description = "Get account number based on the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/myaccount")
    public ResponseEntity<Map<String, String>> getAccountNumber(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getAccountNumber(authentication));
    }

    @PostMapping("/transfer")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SuccessTransfer> transferMoney(@RequestBody TransferMoneyRequest transferMoneyRequest, Authentication authentication) {
        return ResponseEntity.ok(transactionService.transfer(authentication, transferMoneyRequest));
    }

    @Operation(summary = "Get account information", description = "Get account username based on the account number")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/account")
    public ResponseEntity<UserAccountInformation> getUserAccountInformation(@RequestBody UserAccountNumberRequest accountInfo) {
        return ResponseEntity.ok(transactionService.getUserAccountInformation(accountInfo.getAccountNumber()));
    }
}

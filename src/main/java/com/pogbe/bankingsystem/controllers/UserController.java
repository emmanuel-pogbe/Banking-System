package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.dto.requests.*;
import com.pogbe.bankingsystem.dto.responses.*;
import com.pogbe.bankingsystem.services.impl.TransactionServiceImpl;
import com.pogbe.bankingsystem.services.interfaces.TransactionService;
import com.pogbe.bankingsystem.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

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

    @Operation(summary = "Transfer funds to another user", description = "Transfer money to another user based on their account number or their username")
    @PostMapping("/transfer")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SuccessTransfer> transferMoney(@RequestBody TransferMoneyRequest transferMoneyRequest, Authentication authentication) {
        return ResponseEntity.ok(transactionService.transfer(authentication, transferMoneyRequest));
    }

    @PostMapping("/transfer/batch")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SuccessTransfer> transferMoneyBatch(@RequestBody BulkTransferRequestDTO bulkTransferRequestDTO, Authentication authentication) {
        return ResponseEntity.ok(transactionService.bulkTransfer(authentication, bulkTransferRequestDTO));
    }

    @Operation(summary = "Get username of user based on account number", description = "Get account username based on the account number")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/account")
    public ResponseEntity<UserAccountInformation> getUserAccountInformation(@RequestBody UserAccountNumberRequest accountInfo) {
        return ResponseEntity.ok(transactionService.getUserAccountInformation(accountInfo.getAccountNumber()));
    }

    @PostMapping("profile/picture")
    public ResponseEntity<GenericSuccessResponse> uploadProfilePicture(@RequestParam("image") MultipartFile image, Authentication authentication) {
        return ResponseEntity.ok(userService.updateProfilePicture(authentication, image));
    }

    @GetMapping("profile/picture")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        byte[] imageBytes = userService.getProfilePicture(authentication);
        String contentType = userService.getProfilePictureContentType(authentication);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }

    @DeleteMapping("profile/picture")
    public ResponseEntity<GenericSuccessResponse> deleteProfilePicture(Authentication authentication) {
        return ResponseEntity.ok(userService.deleteProfilePicture(authentication));
    }
}

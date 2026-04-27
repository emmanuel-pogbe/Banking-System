package com.pogbe.bankingsystem.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    @Schema(description = "Unique username", example = "emeka")
    private String username;
    @Schema(description = "Unique phone number", example = "0700000000")
    private String phoneNumber;
    @Schema(description = "Password", example = "<PASSWORD>")
    private String password;
    @Schema(description = "Full name", example = "<NAME>")
    private String fullName;
    @Schema(description = "Your account pin", example = "1234")
    private String accountPin;
}

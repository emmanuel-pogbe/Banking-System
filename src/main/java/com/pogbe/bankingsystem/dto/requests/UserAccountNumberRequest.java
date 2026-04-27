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
public class UserAccountNumberRequest {
    @Schema(description = "Account number", example = "123456789")
    private String accountNumber;
    
}

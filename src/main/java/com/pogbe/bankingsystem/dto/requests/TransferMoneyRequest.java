package com.pogbe.bankingsystem.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyRequest {
    @Schema(description = "Receiver's account number or username", example = "pogbe")
    private String receiver;
    @Schema(description = "Your account pin", example = "1234")
    private String pin;
    @Schema(description = "Amount to transfer", example = "1000")
    private BigDecimal amount;
}

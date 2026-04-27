package com.pogbe.bankingsystem.dto.requests;

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
    private String receiverAccountNumber;
    private String pin;
    private BigDecimal amount;
}

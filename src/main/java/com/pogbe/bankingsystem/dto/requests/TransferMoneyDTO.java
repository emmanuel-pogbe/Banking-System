package com.pogbe.bankingsystem.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyDTO {
    protected String receiver;
    protected BigDecimal amount;
}

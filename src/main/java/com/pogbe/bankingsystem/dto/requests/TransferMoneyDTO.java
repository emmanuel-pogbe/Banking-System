package com.pogbe.bankingsystem.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyDTO {
    private String receiver;
    private String amount;
}

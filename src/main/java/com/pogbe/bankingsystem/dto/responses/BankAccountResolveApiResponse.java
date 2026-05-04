package com.pogbe.bankingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResolveApiResponse {
    private Boolean status;
    private String message;
    private BankAccountResolveResponse data;
}

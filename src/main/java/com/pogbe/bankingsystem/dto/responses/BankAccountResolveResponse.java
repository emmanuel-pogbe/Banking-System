package com.pogbe.bankingsystem.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResolveResponse {
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("bank_code")
    private String bankCode;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("account_name")
    private String accountName;
}

package com.pogbe.bankingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDataDTO {
    private String name;
    private String slug;
    private String code;
    private String nibss_bank_code;
    private String country;
}

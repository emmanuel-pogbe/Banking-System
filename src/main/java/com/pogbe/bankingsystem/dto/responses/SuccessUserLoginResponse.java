package com.pogbe.bankingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessUserLoginResponse {
    private String accessToken;
    private String username;
    private BigDecimal accountBalance;
}

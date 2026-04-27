package com.pogbe.bankingsystem.dto.responses;

import com.pogbe.bankingsystem.constants.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecordItemResponse {
    private String transactionReference;
    private TransactionType transactionType;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
}

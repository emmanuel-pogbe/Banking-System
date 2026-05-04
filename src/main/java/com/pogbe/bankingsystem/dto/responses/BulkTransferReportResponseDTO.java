package com.pogbe.bankingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkTransferReportResponseDTO {
    private String message;
    private BigDecimal totalPayout;
    private int totalPeoplePaid;
    
}

package com.pogbe.bankingsystem.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkTransferRequestDTO {
    private String pin;
    private List<TransferMoneyDTO> transfers;
}

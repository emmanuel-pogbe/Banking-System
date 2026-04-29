package com.pogbe.bankingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanksListApiDTO {
    private String status;
    private String message;
    private List<BankDataDTO> data;
}

package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.requests.TransferMoneyRequest;
import com.pogbe.bankingsystem.dto.responses.BanksListApiDTO;
import com.pogbe.bankingsystem.dto.responses.SuccessTransfer;
import com.pogbe.bankingsystem.dto.responses.UserAccountInformation;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionService {

    SuccessTransfer transfer(Authentication authentication, TransferMoneyRequest transferMoneyRequest);

    Map<String, BigDecimal> getAccountBalance(Authentication authentication);

    Map<String, String> getAccountNumber(Authentication authentication);

    UserAccountInformation getUserAccountInformation(String accountNumber);

    BanksListApiDTO getListOfSupportedBanks();
}

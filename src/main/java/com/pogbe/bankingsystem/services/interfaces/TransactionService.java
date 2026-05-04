package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.requests.BulkTransferRequestDTO;
import com.pogbe.bankingsystem.dto.requests.TransferMoneyDTO;
import com.pogbe.bankingsystem.dto.requests.TransferMoneyRequest;
import com.pogbe.bankingsystem.dto.responses.BanksListApiDTO;
import com.pogbe.bankingsystem.dto.responses.BulkTransferReportResponseDTO;
import com.pogbe.bankingsystem.dto.responses.SuccessTransfer;
import com.pogbe.bankingsystem.dto.responses.UserAccountInformation;
import com.pogbe.bankingsystem.models.Account;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Map;

public interface TransactionService {

    SuccessTransfer transfer(Authentication authentication, TransferMoneyRequest transferMoneyRequest);

    Map<String, BigDecimal> getAccountBalance(Authentication authentication);

    Map<String, String> getAccountNumber(Authentication authentication);

    UserAccountInformation getUserAccountInformation(String accountNumber);

    BanksListApiDTO getListOfSupportedBanks();

    BulkTransferReportResponseDTO bulkTransfer(Authentication authentication, BulkTransferRequestDTO bulkTransferRequestDTO);

    Map<String,String> transferLogic(Account senderAccount, TransferMoneyRequest transferMoneyRequest);

    Map<String,String> transferLogicWithoutPin(Account senderAccount, TransferMoneyDTO transferMoneyDTO);
}

package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.responses.SuccessTransfer;
import org.springframework.security.core.Authentication;

public interface TransactionService {

    SuccessTransfer transfer(Authentication authentication, String receiverAccountNumber, double amount, String pin);
}

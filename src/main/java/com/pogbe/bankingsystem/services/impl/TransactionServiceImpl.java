package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.dto.responses.SuccessTransfer;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.TransactionRecord;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.AccountRepository;
import com.pogbe.bankingsystem.repositories.TransactionRecordRepository;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.AesEncryptionService;
import com.pogbe.bankingsystem.services.interfaces.TransactionService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final UserModelRepository userModelRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final AesEncryptionService aesEncryptionService;

    public TransactionServiceImpl(
            AccountRepository accountRepository,
            UserModelRepository userModelRepository,
            TransactionRecordRepository transactionRecordRepository,
            AesEncryptionService aesEncryptionService
    ) {
        this.accountRepository = accountRepository;
        this.userModelRepository = userModelRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.aesEncryptionService = aesEncryptionService;
    }

    @Override
    @Transactional
    public SuccessTransfer transfer(Authentication authentication, String receiverAccountNumber, double amount, String pin) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthenticated request");
        }
        if (receiverAccountNumber == null || receiverAccountNumber.isBlank()) {
            throw new IllegalArgumentException("Receiver account number is required");
        }
        if (pin == null || pin.isBlank()) {
            throw new IllegalArgumentException("Account pin is required");
        }
        if (pin.length() != 4) {
            throw new IllegalArgumentException("Account pin must be 4 digits");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        Account senderAccount = getSenderAccount(authentication);

        String decryptedStoredPin = aesEncryptionService.decrypt(senderAccount.getAccountPin());
        if (!pin.equals(decryptedStoredPin)) {
            throw new IllegalArgumentException("Invalid account pin");
        }

        String encryptedReceiverAccountNumber = aesEncryptionService.encrypt(receiverAccountNumber);
        Account receiverAccount = accountRepository.findByAccountNumber(encryptedReceiverAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        if (senderAccount.getId() == receiverAccount.getId()) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        BigDecimal senderBalance = senderAccount.getAccountBalance() == null
                ? BigDecimal.ZERO
                : senderAccount.getAccountBalance();
        if (senderBalance.compareTo(transferAmount) < 0) {
            throw new IllegalArgumentException("Insufficient account balance");
        }

        BigDecimal receiverBalance = receiverAccount.getAccountBalance() == null
                ? BigDecimal.ZERO
                : receiverAccount.getAccountBalance();

        senderAccount.setAccountBalance(senderBalance.subtract(transferAmount));
        receiverAccount.setAccountBalance(receiverBalance.add(transferAmount));
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        String transferReference = "TXN-" + UUID.randomUUID();

        TransactionRecord debitRecord = new TransactionRecord();
        debitRecord.setDate(LocalDateTime.now());
        debitRecord.setTransactionType(TransactionType.DEBIT);
        debitRecord.setDescription("Transfer to account ending " + receiverAccount.getLastThreeDigits());
        debitRecord.setAmount(transferAmount);
        debitRecord.setTransactionReference(transferReference + "-D");
        debitRecord.setSenderAccount(senderAccount);
        debitRecord.setReceiverAccount(receiverAccount);

        TransactionRecord creditRecord = new TransactionRecord();
        creditRecord.setDate(LocalDateTime.now());
        creditRecord.setTransactionType(TransactionType.CREDIT);
        creditRecord.setDescription("Transfer from account ending " + senderAccount.getLastThreeDigits());
        creditRecord.setAmount(transferAmount);
        creditRecord.setTransactionReference(transferReference + "-C");
        creditRecord.setSenderAccount(senderAccount);
        creditRecord.setReceiverAccount(receiverAccount);

        transactionRecordRepository.save(debitRecord);
        transactionRecordRepository.save(creditRecord);

        return new SuccessTransfer(transferAmount, authentication.getName());
    }

    private Account getSenderAccount(Authentication authentication) {
        Object details = authentication.getDetails();
        if (details instanceof Claims claims) {
            Long accountId = claims.get("accountId", Long.class);
            if (accountId != null) {
                Optional<Account> senderByClaim = accountRepository.findById(accountId);
                if (senderByClaim.isPresent()) {
                    return senderByClaim.get();
                }
            }
        }
        // fallback
        String username = authentication.getName();
        UserModel user = userModelRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
        if (user.getUserAccount() == null) {
            throw new IllegalArgumentException("Authenticated user does not have an account");
        }
        return user.getUserAccount();
    }
}
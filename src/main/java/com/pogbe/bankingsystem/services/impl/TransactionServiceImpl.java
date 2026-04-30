package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.dto.requests.BulkTransferRequestDTO;
import com.pogbe.bankingsystem.dto.requests.TransferMoneyRequest;
import com.pogbe.bankingsystem.dto.responses.BanksListApiDTO;
import com.pogbe.bankingsystem.dto.responses.SuccessTransfer;
import com.pogbe.bankingsystem.dto.responses.UserAccountInformation;
import com.pogbe.bankingsystem.exceptions.custom.ResourceNotAvailable;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.TransactionRecord;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.AccountRepository;
import com.pogbe.bankingsystem.repositories.TransactionRecordRepository;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.AesEncryptionService;
import com.pogbe.bankingsystem.services.interfaces.TransactionService;
import com.pogbe.bankingsystem.utils.NumericUtils;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final UserModelRepository userModelRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final AesEncryptionService aesEncryptionService;
    private final JobOperator jobOperator;
    private final Job job;

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Value("${kora.base}")
    String koraBaseUrl;

    @Value("${kora.public}")
    String koraPublicKey;

    public TransactionServiceImpl(
            AccountRepository accountRepository,
            UserModelRepository userModelRepository,
            TransactionRecordRepository transactionRecordRepository,
            AesEncryptionService aesEncryptionService,
            JobOperator jobOperator,
            Job job

    ) {
        this.accountRepository = accountRepository;
        this.userModelRepository = userModelRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.jobOperator = jobOperator;
        this.job = job;
    }

    @Override
    @Transactional
    public SuccessTransfer transfer(Authentication authentication, TransferMoneyRequest transferMoneyRequest) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthenticated request");
        }
        String receiverDetail = transferMoneyRequest.getReceiver();
        if (receiverDetail == null || receiverDetail.isBlank()) {
           throw new IllegalArgumentException("Receiver account number or username is required");
        }
        String pin = transferMoneyRequest.getPin();
        if (pin == null || pin.isBlank()) {
            throw new IllegalArgumentException("Account pin is required");
        }
        if (pin.length() != 4) {
            throw new IllegalArgumentException("Account pin must be 4 digits");
        }
        if (transferMoneyRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        BigDecimal transferAmount = transferMoneyRequest.getAmount();
        Account senderAccount = getSenderAccount(authentication);

        String decryptedStoredPin = aesEncryptionService.decrypt(senderAccount.getAccountPin());
        if (!pin.equals(decryptedStoredPin)) {
            throw new IllegalArgumentException("Invalid account pin");
        }
        Account receiverAccount = null;
        if (NumericUtils.isNumeric(receiverDetail)) {
            String encryptedReceiverAccountNumber = aesEncryptionService.encrypt(transferMoneyRequest.getReceiver());
            receiverAccount = accountRepository.findByAccountNumber(encryptedReceiverAccountNumber)
                    .orElseThrow(() -> new ResourceNotAvailable("Receiver account not found"));
        }
        else {
            receiverAccount = accountRepository.findByUserUsername(receiverDetail)
                    .orElseThrow(() -> new ResourceNotAvailable("Receiver user not found"));
        }
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
        String sentTo =receiverAccount.getUser().getUsername();
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

        return new SuccessTransfer(transferAmount, authentication.getName(), sentTo);
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
        log.info("Fallback to using username");
        String username = authentication.getName();
        UserModel user = userModelRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
        if (user.getUserAccount() == null) {
            throw new IllegalArgumentException("Authenticated user does not have an account");
        }
        return user.getUserAccount();
    }

    @Override
    public Map<String, BigDecimal> getAccountBalance(Authentication authentication) {
        return Map.of("accountBalance", getSenderAccount(authentication).getAccountBalance());
    }

    @Override
    public Map<String, String> getAccountNumber(Authentication authentication) {
        return Map.of("accountNumber",aesEncryptionService.decrypt(getSenderAccount(authentication).getAccountNumber()));
    }

    @Override
    public UserAccountInformation getUserAccountInformation(String accountNumber) {
        log.info("Account number: {}", accountNumber);
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }
        log.info("Account number length: {}", accountNumber.length());
        if (!NumericUtils.isNumeric(accountNumber) || accountNumber.length() != 10) {
            throw new IllegalArgumentException("Account number is invalid. It must be 10 digits long, contain only numbers and be a String");
        }
        String encryptedAccountNumber = aesEncryptionService.encrypt(accountNumber);
        Optional<Account> account = accountRepository.findByAccountNumber(encryptedAccountNumber);
        if (account.isEmpty()) {
            throw new ResourceNotAvailable("Account not found");
        }
        return new UserAccountInformation(
                account.get().getUser().getUsername()
            );
    }

    @Override
    public BanksListApiDTO getListOfSupportedBanks() {
        String bankUrl = koraBaseUrl + "/banks?countryCode=NG";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(koraPublicKey);
        System.out.println(koraPublicKey);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate request = new RestTemplate();
        return request.exchange(bankUrl, HttpMethod.GET,requestEntity,BanksListApiDTO.class).getBody();
    }

    @Override
    public SuccessTransfer bulkTransfer(Authentication authentication, BulkTransferRequestDTO bulkTransferRequestDTO) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("dto", authentication.getName())
                .addLong("time",System.currentTimeMillis())
                        .toJobParameters();
        try {
            System.out.println("Before job starts");
                jobOperator.start(job, jobParameters);
        } catch (Exception e) {
            System.out.println("Error, I'll do better later");
        }
        System.out.println("Bulk transfer successful");
        return new SuccessTransfer(BigDecimal.ZERO, authentication.getName(), "Bulk transfer started");
    }
}
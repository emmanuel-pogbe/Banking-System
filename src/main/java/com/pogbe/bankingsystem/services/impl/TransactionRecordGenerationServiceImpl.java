package com.pogbe.bankingsystem.services.impl;

import com.opencsv.CSVWriter;
import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.dto.requests.TransactionGenerationRequest;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import com.pogbe.bankingsystem.dto.responses.TransactionRecordItemResponse;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.TransactionRecord;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.AccountRepository;
import com.pogbe.bankingsystem.repositories.TransactionRecordRepository;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.TransactionRecordGenerationService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TransactionRecordGenerationServiceImpl implements TransactionRecordGenerationService {

	private final AccountRepository accountRepository;
	private final UserModelRepository userModelRepository;
	private final TransactionRecordRepository transactionRecordRepository;

	private static final Logger LOG = LoggerFactory.getLogger(TransactionRecordGenerationServiceImpl.class);

	public TransactionRecordGenerationServiceImpl(
			AccountRepository accountRepository,
			UserModelRepository userModelRepository,
			TransactionRecordRepository transactionRecordRepository
	) {
		this.accountRepository = accountRepository;
		this.userModelRepository = userModelRepository;
		this.transactionRecordRepository = transactionRecordRepository;
	}

	@Override
	public PaginatedTransactionRecordsResponse getAccountRecords(
			Authentication authentication,
            TransactionGenerationRequest transactionGenerationRequest
	) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalArgumentException("Unauthenticated request");
		}
		int safePage = Math.max(transactionGenerationRequest.getPage(), 0);
		int safeSize = Math.max(transactionGenerationRequest.getSize(), 1);
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "date"));
		Account account = getSenderAccount(authentication);
		TransactionType transactionType = parseTransactionType(transactionGenerationRequest.getType());
		LocalDateTime startDateTime = parseStartDate(transactionGenerationRequest.getStart());
		LocalDateTime endDateTime = parseEndDate(transactionGenerationRequest.getEnd());
		if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}
		Page<TransactionRecord> records = transactionRecordRepository
				.findStatementByAccountId(account.getId(), transactionType, startDateTime, endDateTime, pageable);
		List<TransactionRecordItemResponse> items = records.getContent()
				.stream()
				.map(record -> new TransactionRecordItemResponse(
						record.getTransactionReference(),
						record.getTransactionType(),
						record.getDescription(),
						record.getAmount(),
						record.getDate()
				))
				.toList();

		return new PaginatedTransactionRecordsResponse(
				items,
				records.getNumber(),
				records.getSize(),
				records.getTotalElements(),
				records.getTotalPages(),
				records.hasNext(),
				records.hasPrevious()
		);
	}

	@Override
	public File getAllAccountRecordsForExport(Authentication authentication) {
		Account account = getSenderAccount(authentication);
		List<TransactionRecord> allTransactions = transactionRecordRepository.findFullStatementByAccountId(account.getId());
		File file = new File("transactions.csv");
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			if (!file.exists() && !file.createNewFile()) {
				throw new IOException("Could not create export file");
			}
			String[] headers = {"Transaction Reference","Transaction Type","Description","Amount","Date"};
			csvWriter.writeNext(headers);
			for (TransactionRecord transaction : allTransactions) {
				String[] row = {transaction.getTransactionReference(),
						transaction.getTransactionType().toString(),
						transaction.getDescription(),
						transaction.getAmount().toString(),
						transaction.getDate().toString()
				};
				csvWriter.writeNext(row);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate transaction export file", e);
		}
		return file;
	}

	private Account getSenderAccount(Authentication authentication) {
		Object details = authentication.getDetails();
		if (details instanceof Claims claims) {
			Long accountId = claims.get("accountId", Long.class);
			if (accountId != null) {
				return accountRepository.findById(accountId)
						.orElseThrow(() -> new IllegalArgumentException("Authenticated account not found"));
			}
		}

		String username = authentication.getName();
		UserModel user = userModelRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

		if (user.getUserAccount() == null) {
			throw new IllegalArgumentException("Authenticated user does not have an account");
		}
		return user.getUserAccount();
	}

	private TransactionType parseTransactionType(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		try {
			return TransactionType.fromString(input.trim());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Transaction type must be either debit or credit");
		}
	}

	private LocalDateTime parseStartDate(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(input.trim()).atStartOfDay();
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("Start date must be in yyyy-MM-dd format");
		}
	}

	private static LocalDateTime parseEndDate(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		try {
			System.err.println(input);
			return LocalDate.parse(input.trim()).atTime(LocalTime.MAX);
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("End date must be in yyyy-MM-dd format");
		}
	}

}

package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.constants.TransactionType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionRecordGenerationServiceImpl implements TransactionRecordGenerationService {

	private final AccountRepository accountRepository;
	private final UserModelRepository userModelRepository;
	private final TransactionRecordRepository transactionRecordRepository;

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
			TransactionType type,
			int page,
			int size
	) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalArgumentException("Unauthenticated request");
		}
		int safePage = Math.max(page, 0);
		int safeSize = Math.max(size, 1);
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "date"));
		Account account = getSenderAccount(authentication);

		Page<TransactionRecord> records = transactionRecordRepository
				.findStatementByAccountId(account.getId(), type, pageable);
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
}

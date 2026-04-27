package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import org.springframework.security.core.Authentication;

public interface TransactionRecordGenerationService {

	PaginatedTransactionRecordsResponse getAccountRecords(
			Authentication authentication,
			TransactionType type,
			int page,
			int size
	);
}

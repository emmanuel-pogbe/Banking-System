package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.requests.TransactionGenerationRequest;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import org.springframework.security.core.Authentication;

public interface TransactionRecordGenerationService {

	PaginatedTransactionRecordsResponse getAccountRecords(
			Authentication authentication,
            TransactionGenerationRequest transactionGenerationRequest
	);

	byte[] getAllAccountRecordsForExport(Authentication authentication, String type);
}

package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.dto.requests.TransactionGenerationRequest;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import com.pogbe.bankingsystem.services.interfaces.TransactionRecordGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionsController {

	private final TransactionRecordGenerationService transactionRecordGenerationService;

	public TransactionsController(TransactionRecordGenerationService transactionRecordGenerationService) {
		this.transactionRecordGenerationService = transactionRecordGenerationService;
	}

	@GetMapping("/records")
	public ResponseEntity<PaginatedTransactionRecordsResponse> getAccountRecords(
            @ModelAttribute TransactionGenerationRequest transactionGenerationRequest,
			Authentication authentication
	) {
		return ResponseEntity.ok(
				transactionRecordGenerationService.getAccountRecords(authentication, transactionGenerationRequest)
		);
	}
}

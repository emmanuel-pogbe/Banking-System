package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.dto.requests.TransactionGenerationRequest;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import com.pogbe.bankingsystem.services.interfaces.TransactionRecordGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionsController {

	private final TransactionRecordGenerationService transactionRecordGenerationService;

	public TransactionsController(TransactionRecordGenerationService transactionRecordGenerationService) {
		this.transactionRecordGenerationService = transactionRecordGenerationService;
	}

	@Operation(summary = "Get account records with filters", description = "Get account records based on some optional filters like date, or transaction type")
	@GetMapping("/records")
	public ResponseEntity<PaginatedTransactionRecordsResponse> getAccountRecords(
            @ModelAttribute TransactionGenerationRequest transactionGenerationRequest,
			Authentication authentication
	) {
		return ResponseEntity.ok(
				transactionRecordGenerationService.getAccountRecords(authentication, transactionGenerationRequest)
		);
	}


	@GetMapping("/records/export")
	public ResponseEntity<byte[]> getAllAccountRecordsForExport(
            @ModelAttribute TransactionGenerationRequest transactionGenerationRequest,
			Authentication authentication
	) {
		byte[] exportFile = transactionRecordGenerationService.getAllAccountRecordsForExport(authentication);
		return ResponseEntity
				.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions1.csv")
				.body(exportFile);
	}
}

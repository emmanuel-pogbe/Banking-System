package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.dto.requests.TransactionGenerationRequest;
import com.pogbe.bankingsystem.dto.responses.BanksListApiDTO;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import com.pogbe.bankingsystem.services.interfaces.TransactionRecordGenerationService;
import com.pogbe.bankingsystem.services.interfaces.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/transactions")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionsController {

	private final TransactionRecordGenerationService transactionRecordGenerationService;
	private final TransactionService transactionService;

	public TransactionsController(TransactionRecordGenerationService transactionRecordGenerationService, TransactionService transactionService) {
		this.transactionRecordGenerationService = transactionRecordGenerationService;
		this.transactionService = transactionService;
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
			@RequestParam(name = "type", required = false) String type,
			Authentication authentication
	) {
		byte[] exportFile = transactionRecordGenerationService.getAllAccountRecordsForExport(authentication, type);
		// defaults
		MediaType mediaType = MediaType.TEXT_PLAIN;
		String filename = "transactions.csv";

		if (type != null && type.equalsIgnoreCase("pdf")) {
			mediaType = MediaType.APPLICATION_PDF;
			filename = "transactions.pdf";
		} else if (type != null && (type.equalsIgnoreCase("csv") || type.isBlank())) {
			mediaType = MediaType.TEXT_PLAIN;
			filename = "transactions.csv";
		}
		return ResponseEntity
				.ok()
				.contentType(mediaType)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(exportFile);
	}

	@GetMapping("/banks")
	public ResponseEntity<BanksListApiDTO> getListOfSupportedBanks() {
		return ResponseEntity.ok(transactionService.getListOfSupportedBanks());
	}
}

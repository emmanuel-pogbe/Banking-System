package com.pogbe.bankingsystem.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionGenerationRequest {
		@Schema(description = "Transaction type", example = "DEBIT")
		private String type;
		@Schema(description = "Page number", example = "1")
		private int page;
		@Schema(description = "Number of records per page", example = "10")
		private int size;
		@Schema(description = "Start date", example = "2026-01-01")
        private String start;
		@Schema(description = "End date", example = "2026-01-31")
        private String end;
    
}

package com.pogbe.bankingsystem.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionGenerationRequest {
		private String type;
		private int page;
		private int size;
        private String start;
        private String end;
    
}

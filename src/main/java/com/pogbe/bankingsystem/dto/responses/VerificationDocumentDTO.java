package com.pogbe.bankingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationDocumentDTO {
    private String documentFileName;
    private String documentType;
}

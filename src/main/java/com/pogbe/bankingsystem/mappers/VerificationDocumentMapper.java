package com.pogbe.bankingsystem.mappers;

import com.pogbe.bankingsystem.dto.responses.VerificationDocumentDTO;
import com.pogbe.bankingsystem.models.VerificationDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VerificationDocumentMapper {
    VerificationDocumentDTO mapToVerificationDocumentDTO(VerificationDocument verificationDocument);

    List<VerificationDocumentDTO> mapToVerificationDocumentDTOs(List<VerificationDocument> verificationDocuments);
}

package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.responses.VerificationDocumentDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DocumentService {
    Map<String, String> uploadDocument(List<MultipartFile> uploadedFiles, Authentication authentication);

    List<VerificationDocumentDTO> getAllVerificationDocumentsByUser(Authentication authentication);
}

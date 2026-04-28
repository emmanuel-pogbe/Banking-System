package com.pogbe.bankingsystem.controllers;

import java.util.List;
import java.util.Map;

import com.pogbe.bankingsystem.dto.responses.VerificationDocumentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pogbe.bankingsystem.services.interfaces.DocumentService;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentUploadController {
    private final DocumentService documentService;

    public DocumentUploadController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadDocument(@RequestParam("image") List<MultipartFile> uploadedFiles, Authentication authentication) {
        return ResponseEntity.ok(documentService.uploadDocument(uploadedFiles, authentication));
    }

    @GetMapping("")
    public ResponseEntity<List<VerificationDocumentDTO>> getAllDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getAllVerificationDocumentsByUser(authentication));
    }
}

package com.pogbe.bankingsystem.controllers;

import java.util.List;
import java.util.Map;

import com.pogbe.bankingsystem.dto.responses.DocumentFileDTO;
import com.pogbe.bankingsystem.dto.responses.VerificationDocumentDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pogbe.bankingsystem.services.interfaces.DocumentService;

@RestController
@RequestMapping("/api/v1/document")
public class VerificationDocumentController {
    private final DocumentService documentService;

    public VerificationDocumentController(DocumentService documentService) {
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

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getDocument(@PathVariable("id") Long id, Authentication authentication) {
        DocumentFileDTO documentFileDTO = documentService.getVerificationDocumentById(id, authentication);
        return ResponseEntity
                .ok()
                .contentType(documentFileDTO.getMediaType())
                .body(documentFileDTO.getDocumentFile());
    }
}

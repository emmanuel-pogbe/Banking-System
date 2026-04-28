package com.pogbe.bankingsystem.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pogbe.bankingsystem.services.interfaces.DocumentService;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentUploadController {
    private DocumentService documentService;

    public DocumentUploadController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadDocument(@RequestParam("image") List<MultipartFile> uploadedFiles, Authentication authentication) {
        return ResponseEntity.ok(documentService.uploadDocument(uploadedFiles, authentication));
    }
}

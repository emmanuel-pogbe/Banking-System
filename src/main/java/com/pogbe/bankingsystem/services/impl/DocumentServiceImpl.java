package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.exceptions.custom.FileHandlingException;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.models.VerificationDocument;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.repositories.VerificationDocumentRepository;
import com.pogbe.bankingsystem.services.interfaces.DocumentService;
import com.pogbe.bankingsystem.utils.FilesValidatorUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final UserModelRepository userModelRepository;
    private final VerificationDocumentRepository verificationDocumentRepository;

    public DocumentServiceImpl(UserModelRepository userModelRepository, VerificationDocumentRepository verificationDocumentRepository) {
        this.userModelRepository = userModelRepository;
        this.verificationDocumentRepository = verificationDocumentRepository;
    }

    @Override
    @Transactional
    public Map<String, String> uploadDocument(List<MultipartFile> uploadedFiles, Authentication authentication) {
        if (uploadedFiles.size() > 5) {
            throw new IllegalArgumentException("You can only upload 5 documents");
        }
        if (uploadedFiles.isEmpty()) {
            throw new IllegalArgumentException("No documents provided");
        }
        UserModel user = getUserFromAuthentication(authentication);
        LOG.info("\n\n\nUploading documents {}\n",uploadedFiles);
        int successCount = 0;
        int failedCount = 0;
        verificationDocumentRepository.deleteAllByUser(user);
        for (MultipartFile file: uploadedFiles) {
            try {
                if (FilesValidatorUtils.isValidFile(file, 1024 * 1024 * 10L, "pdf")) {
                    VerificationDocument document = new VerificationDocument();
                    document.setDocument(file.getBytes());
                    document.setDocumentType(file.getContentType());
                    document.setDocumentFileName(file.getOriginalFilename());
                    document.setUser(user);
                    verificationDocumentRepository.save(document);
                    successCount++;
                } else {
                    failedCount++;
                }
            } catch (IOException e) {
                throw new FileHandlingException("IO Error while reading files");
            }
        }
        return Map.of("message", "Documents uploaded successfully", "successCount", String.valueOf(successCount), "failedCount", String.valueOf(failedCount));
    }

    protected UserModel getUserFromAuthentication(Authentication authentication) {
        return userModelRepository.findByUsername(authentication.getName()).orElseThrow(()->new IllegalArgumentException("Authentication failed: Invalid username or password"));
    }
}

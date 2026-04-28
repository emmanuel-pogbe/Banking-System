package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.dto.responses.DocumentFileDTO;
import com.pogbe.bankingsystem.dto.responses.VerificationDocumentDTO;
import com.pogbe.bankingsystem.exceptions.custom.FileHandlingException;
import com.pogbe.bankingsystem.mappers.VerificationDocumentMapper;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.models.VerificationDocument;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.repositories.VerificationDocumentRepository;
import com.pogbe.bankingsystem.services.interfaces.DocumentService;
import com.pogbe.bankingsystem.utils.FilesValidatorUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
    private final VerificationDocumentMapper verificationDocumentMapper;

    public DocumentServiceImpl(UserModelRepository userModelRepository,
                               VerificationDocumentRepository verificationDocumentRepository,
                               VerificationDocumentMapper verificationDocumentMapper
    ) {
        this.userModelRepository = userModelRepository;
        this.verificationDocumentRepository = verificationDocumentRepository;
        this.verificationDocumentMapper = verificationDocumentMapper;
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

        if (!FilesValidatorUtils.areValidFiles(uploadedFiles, 1024*1024*30L, "pdf")) {
            throw new FileHandlingException("A file is invalid, all files must be of type PDF and less than 30MB");
        }
        verificationDocumentRepository.deleteAllByUser(user);
        for (MultipartFile file: uploadedFiles) {
            try {
                VerificationDocument document = new VerificationDocument();
                document.setDocument(file.getBytes());
                document.setDocumentType(file.getContentType());
                document.setDocumentFileName(file.getOriginalFilename());
                document.setUser(user);
                verificationDocumentRepository.save(document);
                successCount++;
            } catch (IOException e) {
                throw new FileHandlingException("IO An error occurred while reading files. Please try again later");
            }
        }
        return Map.of("message", "Documents uploaded successfully", "successCount", String.valueOf(successCount));
    }

    @Override
    public List<VerificationDocumentDTO> getAllVerificationDocumentsByUser(Authentication authentication) {
        UserModel user = getUserFromAuthentication(authentication);
        List<VerificationDocument> documents = verificationDocumentRepository.findAllByUser(user);
        LOG.info("First documents name {}",documents.get(0).getDocumentFileName());
        return verificationDocumentMapper.mapToVerificationDocumentDTOs(documents);
    }

    @Override
    public DocumentFileDTO getVerificationDocumentById(Long id, Authentication authentication) {
        UserModel user = getUserFromAuthentication(authentication);
        VerificationDocument document = verificationDocumentRepository.findByIdAndUser(id,user)
                .orElseThrow(()->new IllegalArgumentException("Document not found"));
        DocumentFileDTO documentFileDTO = new DocumentFileDTO();
        documentFileDTO.setDocumentFile(document.getDocument());
        try {
        documentFileDTO.setMediaType(MediaType.parseMediaType(document.getDocumentType()));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse media type");
        }
        return documentFileDTO;
    }

    protected UserModel getUserFromAuthentication(Authentication authentication) {
        return userModelRepository.findByUsername(authentication.getName()).orElseThrow(()->new IllegalArgumentException("Authentication failed: Invalid username or password"));
    }
}

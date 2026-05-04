package com.pogbe.bankingsystem.controllers;

import com.pogbe.bankingsystem.services.interfaces.RsaEncryptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/secret")
public class SecureCommunicationController {
    private final RsaEncryptionService rsaEncryptionService;

    public SecureCommunicationController(RsaEncryptionService rsaEncryptionService) {
        this.rsaEncryptionService = rsaEncryptionService;
    }

    @GetMapping("/public-key")
    public ResponseEntity<Map<String,String>> getPublicKey() {
        return ResponseEntity.ok(rsaEncryptionService.getPublicKey());
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String,String>> sendMessage(@RequestBody Map<String,String> message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return ResponseEntity.ok(rsaEncryptionService.decryptMessage(message.get("message")));
    }
}

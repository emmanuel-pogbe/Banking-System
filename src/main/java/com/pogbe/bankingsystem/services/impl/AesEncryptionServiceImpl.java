package com.pogbe.bankingsystem.services.impl;

import com.pogbe.bankingsystem.exceptions.custom.NullKeyStringException;
import com.pogbe.bankingsystem.services.interfaces.AesEncryptionService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AesEncryptionServiceImpl implements AesEncryptionService {
    private final String keyString;
    private final SecretKeySpec secretKeySpec;

    public AesEncryptionServiceImpl(@Value("${aes.encryption.key}") String keyString) {
        this.keyString = keyString;
        byte[] key = keyString.getBytes(StandardCharsets.UTF_8);
        this.secretKeySpec = new SecretKeySpec(key, "AES");
    }
    @Override
    public String encrypt(String plainText) throws RuntimeException {
        if (!keyString.isEmpty()) {
            Cipher cipher;
            try {
                cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
                throw new RuntimeException("Error while encrypting: " + plainText, e);
            }
        }
        else {
            throw new NullKeyStringException("Configuration error, key not set");
        }
    }

    @Override
    public String decrypt(String cipherText) throws RuntimeException {
        if (!keyString.isEmpty()) {
            Cipher cipher;
            try {
                cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("Error while decrypting: " + cipherText, e);
            }
        }
        else {
            throw new NullKeyStringException("Configuration error, key not set");
        }
    }
}

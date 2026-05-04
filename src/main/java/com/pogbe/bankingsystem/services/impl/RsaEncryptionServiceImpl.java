package com.pogbe.bankingsystem.services.impl;

import com.fasterxml.jackson.databind.InjectableValues.Base;
import com.pogbe.bankingsystem.services.interfaces.RsaEncryptionService;
import io.micrometer.core.instrument.config.validate.Validated;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;

@Service
public class RsaEncryptionServiceImpl implements RsaEncryptionService {
    private String publicKey;

    private PublicKey secretPublicKey;
    private PrivateKey secretPrivateKey;

    public RsaEncryptionServiceImpl(@Value("${rsa.encryption.public}") String publicKey, @Value("${rsa.encryption.private}") String privateKey) {
        this.publicKey = publicKey;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            byte[] pubBytes = Base64.getDecoder().decode(publicKey.replaceAll("\n", "").trim());
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubBytes);
            this.secretPublicKey = kf.generatePublic(pubSpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse RSA public key: " + e.getMessage(), e);
        }

        // Try to parse private key, but don't fail startup if it's an unsupported format (e.g. PKCS#1)
        try {
            byte[] privBytes = Base64.getDecoder().decode(privateKey.replaceAll("\n", "").trim());
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privBytes);
            KeyFactory kf2 = KeyFactory.getInstance("RSA");
            this.secretPrivateKey = kf2.generatePrivate(privSpec);
        } catch (Exception e) {
            // Leave private key null and log a clear message — decryption will fail with guidance
            System.out.println("Check secret key ohhh");
            this.secretPrivateKey = null;
            System.err.println("Warning: RSA private key could not be parsed as PKCS#8. Decryption will be unavailable. "
                    + "Convert the private key to PKCS#8 (openssl pkcs8 -topk8 ...) or use a PKCS#1 parser.");
        }
    }
    @Override
    public Map<String, String> getPublicKey() {
        return Map.of("public-key",publicKey);
    }

    @Override
    public Map<String, String> decryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (this.secretPrivateKey == null) {
            throw new RuntimeException("Private key not available or not in PKCS#8 format. Convert to PKCS#8 or enable PKCS#1 parsing.");
        }
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, secretPrivateKey);
        byte[] secretMessageBytes = Base64.getDecoder().decode(message);
        try {
            byte[] decryptedMessageBytes = cipher.doFinal(secretMessageBytes);
            String decryptedMessage = new String(Base64.getDecoder().decode(new String(decryptedMessageBytes)));
            return Map.of("decrypted-message", new String(decryptedMessage));
        } catch (Exception e ) {
            throw new RuntimeException("Error decrypting message", e);
        }
    }

    @Override
    public Map<String, String> encryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, secretPublicKey);
        message = Base64.getEncoder().encodeToString(message.getBytes());
        System.out.println("Encoded message to encrypt: " + message);
        byte[] secretMessageBytes = message.getBytes();
        try {
            byte[] encryptedBytes = cipher.doFinal(secretMessageBytes);
            String encoded = Base64.getEncoder().encodeToString(encryptedBytes);
            return Map.of("encrypted-message", encoded);
        } catch (Exception e ) {
            throw new RuntimeException("Error encrypting message", e);
        }
    }

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
            // Instead of Properties, use Yaml from SnakeYAML
        String testPublicKey;
        String testPrivateKey;
            Yaml yaml = new Yaml();
            try (InputStream input = RsaEncryptionServiceImpl.class.getClassLoader().getResourceAsStream("application.yml")) {
                if (input == null) {
                    System.err.println("application.yml not found");
                    return;
                }
                Map<String, Object> yamlMap = yaml.load(input);
                Map<String, Object> rsa = (Map<String, Object>) yamlMap.get("rsa");
                Map<String, Object> encryption = (Map<String, Object>) rsa.get("encryption");
                testPublicKey = (String) encryption.get("public");
                testPrivateKey = (String) encryption.get("private");
            }
            
            if (testPublicKey == null || testPrivateKey == null) {
                System.err.println("Missing RSA encryption keys in application.properties");
                return;
            }
            
            // Create an instance with actual keys from properties
            RsaEncryptionServiceImpl encryptionService = new RsaEncryptionServiceImpl(testPublicKey, testPrivateKey);
            
            // Test the encryptMessage method
            String messageToDecrypt = "Howdy";
            System.out.println("Original encrypted message: " + messageToDecrypt);
            
            String decryptedMessage = encryptionService.encryptMessage(messageToDecrypt).get("encrypted-message");
            System.out.println("Encrypted message: " + decryptedMessage);
        }
}


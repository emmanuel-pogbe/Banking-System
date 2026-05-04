package com.pogbe.bankingsystem.services.interfaces;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface RsaEncryptionService {
    Map<String, String> getPublicKey();

    Map<String, String> decryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    Map<String, String> encryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException;
}

package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.exceptions.custom.NullKeyStringException;

public interface AesEncryptionService {
    String encrypt(String plainText) throws NullKeyStringException;

    String decrypt(String cipherText) throws NullKeyStringException;
}

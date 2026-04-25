package com.pogbe.bankingsystem.utils;

import java.security.SecureRandom;

public class AccountNumberGenerator {
    public static String generateAccountNumber() {
        SecureRandom secureRandom= new SecureRandom();
        long number = 1000000000L + (long)(secureRandom.nextDouble()*9000000000L);
        return Long.toString(number);
    }
}

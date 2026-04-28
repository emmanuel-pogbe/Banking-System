package com.pogbe.bankingsystem.utils;

public class PasswordValidatorUtils {

    public static String getInvalidPasswordMessage() {
        return "Password must be at least 8 characters, contain at least one lowercase letter, one uppercase letter and one digit";
    }


    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // must be at least 8 characters
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;

        String specialChars = "@$!%*?&";

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
         // will change back later
         return hasLower && hasUpper && hasDigit;
    }
}

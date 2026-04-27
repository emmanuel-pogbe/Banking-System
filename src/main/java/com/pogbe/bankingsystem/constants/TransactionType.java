package com.pogbe.bankingsystem.constants;

public enum TransactionType {
    DEBIT("debit"), 
    CREDIT("credit");

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    // Lookup by string (case-insensitive)
    public static TransactionType fromString(String input) {
        for (TransactionType type : TransactionType.values()) {
            if (type.code.equalsIgnoreCase(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + input);
    }
}

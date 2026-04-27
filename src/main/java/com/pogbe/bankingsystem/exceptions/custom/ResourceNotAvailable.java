package com.pogbe.bankingsystem.exceptions.custom;

public class ResourceNotAvailable extends RuntimeException {
    public ResourceNotAvailable(String message) {
        super(message);
    }
}

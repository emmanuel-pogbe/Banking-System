package com.pogbe.bankingsystem.exceptions;

import com.pogbe.bankingsystem.dto.responses.ErrorResponse;
import com.pogbe.bankingsystem.exceptions.custom.NullKeyStringException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildErrorResponse(
                "DATA_INTEGRITY_VIOLATION",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {NullKeyStringException.class})
    public ResponseEntity<ErrorResponse> handleNullKeyStringException(NullKeyStringException ex, HttpServletRequest request) {
        return buildErrorResponse(
                "NULL_KEY_STRING",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    // This should ideally never be reached
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex,HttpServletRequest request) {
        return buildErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Something went wrong.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String errorCode,
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, message, status.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    
}

package com.pogbe.bankingsystem.exceptions;

import com.pogbe.bankingsystem.dto.responses.ErrorResponse;
import com.pogbe.bankingsystem.exceptions.custom.NullKeyStringException;
import com.pogbe.bankingsystem.security.SecurityConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        ResponseEntity<ErrorResponse> result = buildErrorResponse(
                "DATA_INTEGRITY_VIOLATION",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );
        log.info("[[]] in error timestamp {}", Instant.now().toEpochMilli());
        return result;
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

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(
                "NO_RESOURCE_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
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

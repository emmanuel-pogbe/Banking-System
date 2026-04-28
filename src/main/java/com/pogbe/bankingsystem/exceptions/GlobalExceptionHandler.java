package com.pogbe.bankingsystem.exceptions;

import com.pogbe.bankingsystem.dto.responses.ErrorResponse;
import com.pogbe.bankingsystem.exceptions.custom.FileHandlingException;
import com.pogbe.bankingsystem.exceptions.custom.NullKeyStringException;
import com.pogbe.bankingsystem.exceptions.custom.ResourceNotAvailable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(Exception ex, HttpServletRequest request) {
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
    public ResponseEntity<ErrorResponse> handleNullKeyStringException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "NULL_KEY_STRING",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }



    @ExceptionHandler({NoResourceFoundException.class, ResourceNotAvailable.class})
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "NO_RESOURCE_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "HTTP_MESSAGE_NOT_READABLE",
                "Missing required request parameters",
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "HTTP_REQUEST_METHOD_NOT_SUPPORTED",
                ex.getMessage(),
                HttpStatus.METHOD_NOT_ALLOWED,
                request
        );
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {FileHandlingException.class, FileSizeLimitExceededException.class, FileUploadException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<ErrorResponse> handleFileHandlingExceptions(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "FILE_PROCESSING_ERROR",
                ex.getMessage(),
                HttpStatus.UNPROCESSABLE_CONTENT,
                request
        );
    }


    @ExceptionHandler(value = {MissingServletRequestPartException.class})
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request) {
        return buildErrorResponse(
                "MISSING_REQUEST_PART",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildErrorResponse(
                "INVALID_REQUEST_PARAMETER",
                "Invalid value for parameter '" + ex.getName() + "'",
                HttpStatus.BAD_REQUEST,
                request
        );
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
        ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("Validation failed");
        return buildErrorResponse(
            "VALIDATION_ERROR",
            message,
            HttpStatus.BAD_REQUEST,
            request
        );
        }

        @ExceptionHandler(BindException.class)
        public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex,
            HttpServletRequest request
        ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("Invalid request parameters");
        return buildErrorResponse(
            "BIND_ERROR",
            message,
            HttpStatus.BAD_REQUEST,
            request
        );
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
        ) {
        String message = ex.getConstraintViolations().stream()
            .findFirst()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .orElse("Constraint violation");
        return buildErrorResponse(
            "CONSTRAINT_VIOLATION",
            message,
            HttpStatus.BAD_REQUEST,
            request
        );
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
        ) {
        return buildErrorResponse(
            "MISSING_REQUEST_PARAMETER",
            "Missing required parameter '" + ex.getParameterName() + "'",
            HttpStatus.BAD_REQUEST,
            request
        );
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
        ) {
        log.error("Unhandled runtime exception", ex);
        return buildErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred. Please try again later.",
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

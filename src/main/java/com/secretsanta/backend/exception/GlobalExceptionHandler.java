package com.secretsanta.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Error response structure
    private Map<String, Object> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("path", path);
        return response;
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("field", error.getField());
                    errorMap.put("message", error.getDefaultMessage());
                    return errorMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input");
        response.put("errors", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle unauthorized access
    @ExceptionHandler({UnauthorizedException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            Exception ex,
            WebRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handle bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid email or password",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handle bad request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            BadRequestException ex,
            WebRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle conflict (duplicate resources)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            ConflictException ex,
            WebRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex,
            WebRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request.getDescription(false).replace("uri=", "")
        );

        // Log the full exception for debugging
        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

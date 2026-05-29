package com.example.device_service.Controller;

import com.example.device_service.Exception.DeviceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorMap(Exception exception, HttpStatus status, String path) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("timestamp", LocalDateTime.now());
        errorMap.put("status", status.value());
        errorMap.put("error", status.getReasonPhrase());
        errorMap.put("message", exception.getMessage());
        errorMap.put("path", path);
        return errorMap;
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleDeviceNotFoundException(
            DeviceNotFoundException deviceNotFoundException,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorMap(deviceNotFoundException, HttpStatus.NOT_FOUND, request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception exception,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorMap(exception, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI()));
    }
}

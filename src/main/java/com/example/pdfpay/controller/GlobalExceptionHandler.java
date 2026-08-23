package com.example.pdfpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handle(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error",
                e.getMessage() == null ? "Unexpected error" : e.getMessage()));
    }
}

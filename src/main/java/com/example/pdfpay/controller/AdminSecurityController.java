package com.example.pdfpay.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminSecurityController {

    @GetMapping("/admin/csrf")
    public Map<String, String> csrf(CsrfToken token) {

        return Map.of(
                "token", token.getToken()
        );
    }
}
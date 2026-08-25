package com.example.pdfpay.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/admin/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
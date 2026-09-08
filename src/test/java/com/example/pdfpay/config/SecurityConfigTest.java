package com.example.pdfpay.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {
    @Test void passwordEncoderHashesPasswords() {
        var encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("ChangeMe123!");
        assertNotEquals("ChangeMe123!", hash);
        assertTrue(encoder.matches("ChangeMe123!", hash));
    }
}

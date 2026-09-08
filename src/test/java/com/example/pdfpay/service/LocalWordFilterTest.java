package com.example.pdfpay.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocalWordFilterTest {
    private final LocalWordFilter filter = new LocalWordFilter();
    @Test void detectsPlainProfanity(){ assertTrue(filter.containsBlockedWords("this is shit")); }
    @Test void detectsObfuscatedProfanity(){ assertTrue(filter.containsBlockedWords("f*ck sh1t")); assertTrue(filter.containsBlockedWords("f u c k")); }
    @Test void allowsNormalTechnicalText(){ assertFalse(filter.containsBlockedWords("How does Spring Boot dependency injection work?")); }
}

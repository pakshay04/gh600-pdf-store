package com.example.pdfpay.service;

import com.example.pdfpay.entity.Payment;
import com.example.pdfpay.entity.PdfDocument;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class DownloadServiceTest {
    @Test void paidPaymentCreatesSingleUseToken() {
        DownloadService service = new DownloadService(30);
        PdfDocument doc = new PdfDocument();
        ReflectionTestUtils.setField(doc, "id", 42L);
        Payment payment = new Payment();
        payment.setStatus("PAID");
        payment.setDocument(doc);
        String token = service.createToken(payment);
        assertEquals(42L, service.consume(token));
        assertThrows(IllegalArgumentException.class, () -> service.consume(token));
    }

    @Test void unpaidPaymentIsRejected() {
        DownloadService service = new DownloadService(30);
        Payment payment = new Payment();
        payment.setStatus("CREATED");
        assertThrows(IllegalArgumentException.class, () -> service.createToken(payment));
    }
}

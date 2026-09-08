package com.example.pdfpay.service;

import com.example.pdfpay.entity.Payment;
import com.example.pdfpay.repository.PaymentRepository;
import com.example.pdfpay.repository.PdfRepository;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentServiceTest {
    private PaymentRepository payments;
    private PdfRepository documents;
    private RazorpayClient razorpay;
    private PaymentService service;

    @BeforeEach void setup() {
        payments = Mockito.mock(PaymentRepository.class);
        documents = Mockito.mock(PdfRepository.class);
        razorpay = Mockito.mock(RazorpayClient.class);
        service = new PaymentService(payments, documents, razorpay, "test-secret");
    }

    @Test void rejectsMissingOrderId() {
        assertThrows(IllegalArgumentException.class, () -> service.verifyPayment(null, "pay", "sig"));
    }

    @Test void rejectsMissingPaymentId() {
        assertThrows(IllegalArgumentException.class, () -> service.verifyPayment("order", null, "sig"));
    }

    @Test void rejectsMissingSignature() {
        assertThrows(IllegalArgumentException.class, () -> service.verifyPayment("order", "pay", ""));
    }
}

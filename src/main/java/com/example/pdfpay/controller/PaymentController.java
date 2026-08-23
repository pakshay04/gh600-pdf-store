package com.example.pdfpay.controller;

import com.example.pdfpay.entity.Payment;
import com.example.pdfpay.service.DownloadService;
import com.example.pdfpay.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final DownloadService downloadService;
    private final String razorpayKeyId;

    public PaymentController(
            PaymentService paymentService,
            DownloadService downloadService,
            @Value("${razorpay.key.id}") String razorpayKeyId) {
        this.paymentService = paymentService;
        this.downloadService = downloadService;
        this.razorpayKeyId = razorpayKeyId;
    }

    @PostMapping("/order/{documentId}")
    public ResponseEntity<?> createOrder(@PathVariable Long documentId) {
        try {
            Payment p = paymentService.createOrder(documentId);
            return ResponseEntity.ok(Map.of(
                    "orderId", p.getOrderId(),
                    "razorpayOrderId", p.getRazorpayOrderId(),
                    "amountPaise", p.getAmountPaise(),
                    "keyId", razorpayKeyId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage() == null ? "Could not create payment order." : e.getMessage()
            ));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            Payment p = paymentService.verifyPayment(
                    request.get("razorpay_order_id"),
                    request.get("razorpay_payment_id"),
                    request.get("razorpay_signature")
            );

            String token = downloadService.createToken(p);

            return ResponseEntity.ok(Map.of(
                    "status", "PAID",
                    "downloadUrl", "/api/download/" + token
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage() == null ? "Payment verification failed." : e.getMessage()
            ));
        }
    }
}

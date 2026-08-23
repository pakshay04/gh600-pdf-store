package com.example.pdfpay.service;

import com.example.pdfpay.entity.Payment;
import com.example.pdfpay.entity.PdfDocument;
import com.example.pdfpay.repository.PaymentRepository;
import com.example.pdfpay.repository.PdfRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository payments;
    private final PdfRepository documents;
    private final RazorpayClient razorpayClient;
    private final String razorpayKeySecret;

    public PaymentService(
            PaymentRepository payments,
            PdfRepository documents,
            RazorpayClient razorpayClient,
            @Value("${razorpay.key.secret}") String razorpayKeySecret) {
        this.payments = payments;
        this.documents = documents;
        this.razorpayClient = razorpayClient;
        this.razorpayKeySecret = razorpayKeySecret;
    }

    public Payment createOrder(Long documentId) throws Exception {
        PdfDocument doc = documents.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("PDF not found."));

        Payment p = new Payment();
        p.setOrderId("ORDER_" + UUID.randomUUID());
        p.setDocument(doc);
        p.setAmountPaise(doc.getPricePaise());
        p.setStatus("CREATED");
        p.setCreatedAt(Instant.now());

        JSONObject options = new JSONObject();
        options.put("amount", doc.getPricePaise());
        options.put("currency", "INR");
        options.put("receipt", p.getOrderId());
        options.put("payment_capture", 1);

        Order razorpayOrder = razorpayClient.orders.create(options);
        p.setRazorpayOrderId(razorpayOrder.get("id"));

        return payments.save(p);
    }

    public Payment verifyPayment(String razorpayOrderId,
                                 String razorpayPaymentId,
                                 String razorpaySignature) throws Exception {
        if (razorpayOrderId == null || razorpayOrderId.isBlank())
            throw new IllegalArgumentException("Missing Razorpay order ID.");
        if (razorpayPaymentId == null || razorpayPaymentId.isBlank())
            throw new IllegalArgumentException("Missing Razorpay payment ID.");
        if (razorpaySignature == null || razorpaySignature.isBlank())
            throw new IllegalArgumentException("Missing Razorpay signature.");

        Payment p = payments.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment order not found."));

        if ("PAID".equals(p.getStatus())) {
            return p;
        }

        if (payments.existsByRazorpayPaymentId(razorpayPaymentId)) {
            throw new IllegalArgumentException("Payment already processed.");
        }

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", p.getRazorpayOrderId());
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        boolean valid = Utils.verifyPaymentSignature(
                attributes, razorpayKeySecret);

        if (!valid) {
            throw new IllegalArgumentException("Invalid Razorpay payment signature.");
        }

        p.setRazorpayPaymentId(razorpayPaymentId);
        p.setRazorpaySignature(razorpaySignature);
        p.setProviderPaymentId(razorpayPaymentId);
        p.setStatus("PAID");

        return payments.save(p);
    }
}

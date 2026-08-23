package com.example.pdfpay.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @ManyToOne(optional = false)
    private PdfDocument document;

    @Column(nullable = false)
    private long amountPaise;

    @Column(nullable = false)
    private String status;

    private String providerPaymentId;

    @Column(unique = true)
    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    @Column(nullable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String v) { this.orderId = v; }
    public PdfDocument getDocument() { return document; }
    public void setDocument(PdfDocument v) { this.document = v; }
    public long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(long v) { this.amountPaise = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getProviderPaymentId() { return providerPaymentId; }
    public void setProviderPaymentId(String v) { this.providerPaymentId = v; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String v) { this.razorpayOrderId = v; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String v) { this.razorpayPaymentId = v; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String v) { this.razorpaySignature = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
}

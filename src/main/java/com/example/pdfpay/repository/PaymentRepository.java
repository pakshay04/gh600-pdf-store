package com.example.pdfpay.repository;

import com.example.pdfpay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    boolean existsByRazorpayPaymentId(String razorpayPaymentId);
}

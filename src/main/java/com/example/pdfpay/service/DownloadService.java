package com.example.pdfpay.service;

import com.example.pdfpay.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DownloadService {
    private final Map<String, TokenData> tokens = new ConcurrentHashMap<>();
    private final long tokenMinutes;

    public DownloadService(@Value("${app.download.token-minutes}") long tokenMinutes) {
        this.tokenMinutes = tokenMinutes;
    }

    public String createToken(Payment payment) {
        if (!"PAID".equals(payment.getStatus()))
            throw new IllegalArgumentException("Payment is not completed.");
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenData(payment.getDocument().getId(), Instant.now().plusSeconds(tokenMinutes * 60)));
        return token;
    }

    public Long consume(String token) {
        TokenData data = tokens.remove(token);
        if (data == null || data.expiresAt().isBefore(Instant.now()))
            throw new IllegalArgumentException("Download link is invalid or expired.");
        return data.documentId();
    }

    private record TokenData(Long documentId, Instant expiresAt) {}
}

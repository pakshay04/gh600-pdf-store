package com.example.pdfpay.repository;

import com.example.pdfpay.entity.PdfDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfRepository extends JpaRepository<PdfDocument, Long> {}

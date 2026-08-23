package com.example.pdfpay.controller;

import com.example.pdfpay.entity.PdfDocument;
import com.example.pdfpay.repository.PdfRepository;
import com.example.pdfpay.service.DownloadService;
import com.example.pdfpay.service.PdfService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/download")
public class DownloadController {
    private final DownloadService downloads;
    private final PdfRepository documents;
    private final PdfService pdfService;

    public DownloadController(DownloadService downloads, PdfRepository documents, PdfService pdfService) {
        this.downloads = downloads;
        this.documents = documents;
        this.pdfService = pdfService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> download(@PathVariable String token) {
        try {
            Long id = downloads.consume(token);
            PdfDocument doc = documents.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("PDF not found."));
            FileSystemResource resource = new FileSystemResource(pdfService.getPath(doc));
            if (!resource.exists()) return ResponseEntity.notFound().build();

            String safeName = doc.getOriginalFilename()
                    .replace("\"", "")
                    .replace("\r", "")
                    .replace("\n", "");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

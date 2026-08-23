package com.example.pdfpay.controller;

import com.example.pdfpay.entity.PdfDocument;
import com.example.pdfpay.repository.PdfRepository;
import com.example.pdfpay.service.PdfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/pdfs")
public class PdfController {
    private final PdfService pdfService;
    private final PdfRepository repository;

    public PdfController(PdfService pdfService, PdfRepository repository) {
        this.pdfService = pdfService;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestHeader("X-Admin-Key") String adminKey,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Long pricePaise) {

        String configuredKey = System.getenv("ADMIN_API_KEY");

        if (configuredKey == null || !configuredKey.equals(adminKey)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Unauthorized"));
        }

        try {
            PdfDocument d = pdfService.upload(file, pricePaise);

            return ResponseEntity.ok(Map.of(
                    "id", d.getId(),
                    "filename", d.getOriginalFilename(),
                    "pricePaise", d.getPricePaise(),
                    "purchaseUrl", "/?pdf=" + d.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(d -> ResponseEntity.ok(Map.of(
                        "id", d.getId(),
                        "filename", d.getOriginalFilename(),
                        "pricePaise", d.getPricePaise())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

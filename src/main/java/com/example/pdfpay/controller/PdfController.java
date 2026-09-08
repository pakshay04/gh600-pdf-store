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
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Long pricePaise) {

        try {

            PdfDocument d =
                    pdfService.upload(file, pricePaise);

            return ResponseEntity.ok(
                    Map.of(
                            "id", d.getId(),
                            "filename", d.getOriginalFilename(),
                            "pricePaise", d.getPricePaise(),
                            "purchaseUrl",
                            "/?pdf=" + d.getId()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }


    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(repository.findAll().stream().map(this::summary).toList());
    }

    @GetMapping("/admin")
    public ResponseEntity<?> adminList() {
        return ResponseEntity.ok(repository.findAll().stream().map(this::summary).toList());
    }

    @PatchMapping("/admin/{id}/price")
    public ResponseEntity<?> updatePrice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Object raw = body.get("pricePaise");
            if (raw == null) throw new IllegalArgumentException("pricePaise is required.");
            long price = Long.parseLong(String.valueOf(raw));
            PdfDocument d = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("PDF not found."));
            return ResponseEntity.ok(summary(pdfService.updatePrice(d, price)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/{id}/file")
    public ResponseEntity<?> replaceFile(@PathVariable Long id, @RequestParam MultipartFile file) {
        try {
            PdfDocument d = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("PDF not found."));
            return ResponseEntity.ok(summary(pdfService.replaceFile(d, file)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> summary(PdfDocument d) {
        return Map.of(
                "id", d.getId(),
                "filename", d.getOriginalFilename(),
                "pricePaise", d.getPricePaise(),
                "sizeBytes", d.getSizeBytes(),
                "createdAt", d.getCreatedAt() == null ? "" : d.getCreatedAt().toString(),
                "purchaseUrl", "/?pdf=" + d.getId()
        );
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

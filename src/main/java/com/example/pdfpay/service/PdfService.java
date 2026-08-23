package com.example.pdfpay.service;

import com.example.pdfpay.entity.PdfDocument;
import com.example.pdfpay.repository.PdfRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.UUID;

@Service
public class PdfService {
    private final PdfRepository repository;
    private final Path storagePath;
    private final long defaultPricePaise;

    public PdfService(PdfRepository repository,
                      @Value("${app.storage.path}") String storage,
                      @Value("${app.default-price-paise}") long defaultPricePaise) {
        this.repository = repository;
        this.storagePath = Paths.get(storage).toAbsolutePath().normalize();
        this.defaultPricePaise = defaultPricePaise;
        try { Files.createDirectories(storagePath); }
        catch (IOException e) { throw new IllegalStateException("Cannot create storage directory", e); }
    }

    public PdfDocument upload(MultipartFile file, Long requestedPricePaise) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Please select a PDF.");
        String name = file.getOriginalFilename() == null ? "document.pdf" : file.getOriginalFilename();
        if (!name.toLowerCase().endsWith(".pdf")) throw new IllegalArgumentException("Only PDF files are allowed.");
        if (file.getSize() > 20L * 1024 * 1024) throw new IllegalArgumentException("Maximum file size is 20 MB.");

        byte[] header = file.getBytes();
        if (header.length < 4 || header[0] != '%' || header[1] != 'P' || header[2] != 'D' || header[3] != 'F')
            throw new IllegalArgumentException("The uploaded file is not a valid PDF.");

        String stored = UUID.randomUUID() + ".pdf";
        Files.write(storagePath.resolve(stored), header, StandardOpenOption.CREATE_NEW);

        PdfDocument doc = new PdfDocument();
        doc.setOriginalFilename(name);
        doc.setStoredFilename(stored);
        doc.setSizeBytes(file.getSize());
        long price = requestedPricePaise == null ? defaultPricePaise : requestedPricePaise;
        if (price < 100) throw new IllegalArgumentException("Minimum price is ₹1.");
        doc.setPricePaise(price);
        doc.setCreatedAt(Instant.now());
        return repository.save(doc);
    }

    public Path getPath(PdfDocument doc) {
        return storagePath.resolve(doc.getStoredFilename()).normalize();
    }
}

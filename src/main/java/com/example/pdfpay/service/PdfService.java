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
        byte[] bytes = validatePdf(file);
        String name = safeName(file);
        String stored = UUID.randomUUID() + ".pdf";
        Files.write(storagePath.resolve(stored), bytes, StandardOpenOption.CREATE_NEW);

        PdfDocument doc = new PdfDocument();
        doc.setOriginalFilename(name);
        doc.setStoredFilename(stored);
        doc.setSizeBytes(file.getSize());
        long price = requestedPricePaise == null ? defaultPricePaise : requestedPricePaise;
        validatePrice(price);
        doc.setPricePaise(price);
        doc.setCreatedAt(Instant.now());
        return repository.save(doc);
    }

    public PdfDocument replaceFile(PdfDocument doc, MultipartFile file) throws IOException {
        byte[] bytes = validatePdf(file);
        String name = safeName(file);
        String newStored = UUID.randomUUID() + ".pdf";
        Path newPath = storagePath.resolve(newStored);
        Files.write(newPath, bytes, StandardOpenOption.CREATE_NEW);

        Path oldPath = getPath(doc);
        doc.setOriginalFilename(name);
        doc.setStoredFilename(newStored);
        doc.setSizeBytes(file.getSize());
        PdfDocument saved = repository.save(doc);
        try { Files.deleteIfExists(oldPath); } catch (IOException ignored) { }
        return saved;
    }

    public PdfDocument updatePrice(PdfDocument doc, long pricePaise) {
        validatePrice(pricePaise);
        doc.setPricePaise(pricePaise);
        return repository.save(doc);
    }

    private byte[] validatePdf(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Please select a PDF.");
        String name = safeName(file);
        if (!name.toLowerCase().endsWith(".pdf")) throw new IllegalArgumentException("Only PDF files are allowed.");
        if (file.getSize() > 20L * 1024 * 1024) throw new IllegalArgumentException("Maximum file size is 20 MB.");
        byte[] bytes = file.getBytes();
        if (bytes.length < 4 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F')
            throw new IllegalArgumentException("The uploaded file is not a valid PDF.");
        return bytes;
    }

    private String safeName(MultipartFile file) {
        String name = file.getOriginalFilename() == null ? "document.pdf" : Paths.get(file.getOriginalFilename()).getFileName().toString();
        return name.isBlank() ? "document.pdf" : name;
    }

    private void validatePrice(long pricePaise) {
        if (pricePaise < 100) throw new IllegalArgumentException("Minimum price is ₹1.");
    }

    public Path getPath(PdfDocument doc) {
        return storagePath.resolve(doc.getStoredFilename()).normalize();
    }
}

package com.example.pdfpay.service;

import com.example.pdfpay.entity.PdfDocument;
import com.example.pdfpay.repository.PdfRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PdfServiceTest {
    @TempDir Path temp;

    @Test void uploadValidPdfStoresFileAndPrice() throws Exception {
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        when(repo.save(any(PdfDocument.class))).thenAnswer(inv -> inv.getArgument(0));
        PdfService service = new PdfService(repo, temp.toString(), 4900);
        MockMultipartFile file = new MockMultipartFile("file", "guide.pdf", "application/pdf", "%PDF-1.7\ncontent".getBytes());
        PdfDocument doc = service.upload(file, 9900L);
        assertEquals("guide.pdf", doc.getOriginalFilename());
        assertEquals(9900L, doc.getPricePaise());
        assertTrue(service.getPath(doc).getParent().toFile().exists());
        assertTrue(java.nio.file.Files.exists(service.getPath(doc)));
    }

    @Test void rejectsNonPdf() {
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfService service = new PdfService(repo, temp.toString(), 4900);
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "hello".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.upload(file, null));
    }

    @Test void rejectsPdfWithInvalidHeader() {
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfService service = new PdfService(repo, temp.toString(), 4900);
        MockMultipartFile file = new MockMultipartFile("file", "fake.pdf", "application/pdf", "not-a-pdf".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.upload(file, null));
    }
}

package com.example.pdfpay.controller;

import com.example.pdfpay.entity.PdfDocument;
import com.example.pdfpay.repository.PdfRepository;
import com.example.pdfpay.service.PdfService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PdfControllerTest {
    @Test void unknownPdfReturns404() throws Exception {
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        when(repo.findById(999L)).thenReturn(Optional.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PdfController(Mockito.mock(PdfService.class), repo)).build();
        mvc.perform(get("/api/pdfs/999")).andExpect(status().isNotFound());
    }

    @Test void publicListReturnsAllMaterials() throws Exception {
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfService service = Mockito.mock(PdfService.class);
        PdfDocument one = doc(1L, "java.pdf", 9900);
        PdfDocument two = doc(2L, "gh-600.pdf", 49900);
        when(repo.findAll()).thenReturn(List.of(one, two));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PdfController(service, repo)).build();
        mvc.perform(get("/api/pdfs")).andExpect(status().isOk());
    }

    @Test void adminListReturnsAllMaterials() throws Exception {
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfService service = Mockito.mock(PdfService.class);
        when(repo.findAll()).thenReturn(List.of(doc(1L, "a.pdf", 10000), doc(2L, "b.pdf", 20000)));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PdfController(service, repo)).build();
        mvc.perform(get("/api/pdfs/admin")).andExpect(status().isOk());
    }

    @Test void uploadDelegatesToService() throws Exception {
        PdfService service = Mockito.mock(PdfService.class);
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfDocument doc = doc(3L, "guide.pdf", 4900);
        when(service.upload(Mockito.any(), Mockito.any())).thenReturn(doc);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PdfController(service, repo)).build();
        MockMultipartFile file = new MockMultipartFile("file", "guide.pdf", MediaType.APPLICATION_PDF_VALUE, "%PDF-1.7".getBytes());
        mvc.perform(multipart("/api/pdfs/upload").file(file).param("pricePaise", "4900")).andExpect(status().isOk());
    }

    @Test void adminPriceUpdateDelegatesToService() throws Exception {
        PdfService service = Mockito.mock(PdfService.class);
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfDocument existing = doc(4L, "old.pdf", 4900);
        when(repo.findById(4L)).thenReturn(Optional.of(existing));
        when(service.updatePrice(existing, 9900)).thenReturn(existing);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PdfController(service, repo)).build();
        mvc.perform(patch("/api/pdfs/admin/4/price").contentType(MediaType.APPLICATION_JSON).content("{\"pricePaise\":9900}"))
                .andExpect(status().isOk());
    }

    @Test void adminFileReplacementDelegatesToService() throws Exception {
        PdfService service = Mockito.mock(PdfService.class);
        PdfRepository repo = Mockito.mock(PdfRepository.class);
        PdfDocument existing = doc(5L, "old.pdf", 4900);
        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(service.replaceFile(Mockito.eq(existing), Mockito.any())).thenReturn(existing);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PdfController(service, repo)).build();
        MockMultipartFile file = new MockMultipartFile("file", "new.pdf", MediaType.APPLICATION_PDF_VALUE, "%PDF-1.7".getBytes());
        mvc.perform(multipart("/api/pdfs/admin/5/file").file(file).with(req -> { req.setMethod("PUT"); return req; }))
                .andExpect(status().isOk());
    }

    private PdfDocument doc(Long id, String name, long price) {
        PdfDocument d = new PdfDocument();
        try { var f = PdfDocument.class.getDeclaredField("id"); f.setAccessible(true); f.set(d, id); } catch (Exception ignored) { }
        d.setOriginalFilename(name);
        d.setStoredFilename(id + ".pdf");
        d.setSizeBytes(1024);
        d.setPricePaise(price);
        return d;
    }
}

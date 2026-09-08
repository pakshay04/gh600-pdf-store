package com.example.pdfpay.controller;

import com.example.pdfpay.service.DownloadService;
import com.example.pdfpay.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {
    @Test void verifyEndpointRejectsMissingPaymentFields() throws Exception {
        PaymentService payments = Mockito.mock(PaymentService.class);
        DownloadService downloads = Mockito.mock(DownloadService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PaymentController(payments, downloads, "rzp_test_dummy")).build();
        mvc.perform(post("/api/payments/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

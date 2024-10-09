package ru.tbank.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.service.CurrencyService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CurrencyController.class})
public class CurrencyControllerTest {

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getCurrencyRateByRate_ValidCode_ReturnsOk() throws Exception {
        mockMvc.perform(get("/currency/rates/{code}", "USD"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCurrencyRateByRate_InvalidCode_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/currency/rates/{code}", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void convertCurrency_ValidRequest_ReturnsOk() throws Exception {
        String dto = """
                {
                    "fromCurrency": "USD",
                    "toCurrency": "EUR",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/currency/convert")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void convertCurrency_InvalidFromCurrency_ReturnsBadRequest() throws Exception {
        String dto = """
                {
                    "fromCurrency": "",
                    "toCurrency": "EUR",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/currency/convert")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void convertCurrency_InvalidToCurrency_ReturnsBadRequest() throws Exception {
        String dto = """
                {
                    "fromCurrency": "USD",
                    "toCurrency": "",
                    "amount": 100.0
                }
                """;

        mockMvc.perform(post("/currency/convert")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void convertCurrency_InvalidAmount_ReturnsBadRequest() throws Exception {
        String dto = """
                {
                    "fromCurrency": "USD",
                    "toCurrency": "EUR",
                    "amount": -100.0
                }
                """;

        mockMvc.perform(post("/currency/convert")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void convertCurrency_NullAmount_ReturnsBadRequest() throws Exception {
        String dto = """
                {
                    "fromCurrency": "USD",
                    "toCurrency": "EUR",
                    "amount": null
                }
                """;

        mockMvc.perform(post("/currency/convert")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

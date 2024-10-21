package ru.tbank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.cbr.curs.ValCursDTO;
import ru.tbank.dto.cbr.item.ItemDTO;
import ru.tbank.dto.cbr.valute.ValuteDTO;
import ru.tbank.dto.cbr.valute.ValuteFullDTO;
import ru.tbank.dto.currency.CurrencyConvertRequestDTO;
import ru.tbank.dto.currency.CurrencyConvertResponseDTO;
import ru.tbank.dto.currency.CurrencyRateResponseDTO;
import ru.tbank.exception.CurrencyRateNotFoundException;
import ru.tbank.exception.NonexistentCurrencyException;
import ru.tbank.integration.http.CBRClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CBRClient cbrClient;

    @InjectMocks
    private CurrencyService currencyService;

    @ParameterizedTest
    @CsvSource({
            "USD, 95.00, 95.00",
            "EUR, 105.00, 105.00"
    })
    void getCurrencyRateByCode_ValidCode_ReturnsRate(String code, String vUnitRate, double expectedVUnitRate) {
        ValCursDTO valCursDTO = new ValCursDTO();
        ValuteDTO valuteDTO = createValuteDTO(code, vUnitRate);
        valCursDTO.setValutes(List.of(valuteDTO));

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        ItemDTO usdItemDTO = createItemDTO(code);
        valuteFullDTO.setItems(List.of(usdItemDTO));

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);
        when(cbrClient.getCurrencyRates()).thenReturn(valCursDTO);

        CurrencyRateResponseDTO response = currencyService.getCurrencyRateByCode(code);

        assertEquals(code, response.getCurrency());
        assertEquals(expectedVUnitRate, response.getRate());
    }

    @Test
    void getCurrencyRateByCode_ValidCodeButRateNotFound_ThrowsException() {
        String code = "ATS";
        ValCursDTO valCursDTO = new ValCursDTO();
        ValuteDTO usdValuteDTO = createValuteDTO("USD", "95.00");
        valCursDTO.setValutes(List.of(usdValuteDTO));

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        ItemDTO usdItemDTO = createItemDTO("USD");
        ItemDTO atsItemDTO = createItemDTO("ATS");
        valuteFullDTO.setItems(List.of(usdItemDTO, atsItemDTO));

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);
        when(cbrClient.getCurrencyRates()).thenReturn(valCursDTO);

        assertThrows(CurrencyRateNotFoundException.class, () -> currencyService.getCurrencyRateByCode(code));
    }

    @Test
    void getCurrencyRateByCode_InvalidCode_ThrowsException() {
        String code = "INVALID";

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        ItemDTO usdItemDTO = createItemDTO("USD");
        valuteFullDTO.setItems(List.of(usdItemDTO));

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);

        assertThrows(NonexistentCurrencyException.class, () -> currencyService.getCurrencyRateByCode(code));
    }

    @ParameterizedTest
    @CsvSource({
            "USD, EUR, 100.0, 90.476",
            "EUR, USD, 100.0, 110.526"
    })
    void convertCurrency_ValidRequest_ReturnsConvertedAmount(
            String fromCurrency, String toCurrency, double amount, double expectedConvertedAmount
    ) {
        ValCursDTO valCursDTO = new ValCursDTO();
        ValuteDTO usdValuteDTO = createValuteDTO("USD", "95.00");
        ValuteDTO eurValuteDTO = createValuteDTO("EUR", "105.00");
        valCursDTO.setValutes(List.of(usdValuteDTO, eurValuteDTO));

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        ItemDTO usdItemDTO = createItemDTO("USD");
        ItemDTO eurItemDTO = createItemDTO("EUR");
        valuteFullDTO.setItems(List.of(usdItemDTO, eurItemDTO));

        when(cbrClient.getCurrencyRates()).thenReturn(valCursDTO);
        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);

        CurrencyConvertRequestDTO requestDTO = createCurrencyConvertRequestDTO(fromCurrency, toCurrency, amount);

        CurrencyConvertResponseDTO responseDTO = currencyService.convertCurrency(requestDTO);

        assertEquals(fromCurrency, responseDTO.getFromCurrency());
        assertEquals(toCurrency, responseDTO.getToCurrency());
        assertEquals(expectedConvertedAmount, responseDTO.getConvertedAmount(), 0.01);
    }

    @Test
    void convertCurrency_InvalidFromCurrency_ThrowsException() {
        CurrencyConvertRequestDTO requestDTO = createCurrencyConvertRequestDTO("INVALID", "USD", 100.0);

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        valuteFullDTO.setItems(List.of());

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);

        assertThrows(NonexistentCurrencyException.class, () -> currencyService.convertCurrency(requestDTO));
    }

    @Test
    void convertCurrency_ValidFromCurrencyButRateNotFound_ThrowsException() {
        CurrencyConvertRequestDTO requestDTO = createCurrencyConvertRequestDTO("ATS", "USD", 100.0);

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        ItemDTO usdItemDTO = createItemDTO("USD");
        ItemDTO atsItemDTO = createItemDTO("ATS");
        valuteFullDTO.setItems(List.of(usdItemDTO, atsItemDTO));

        ValCursDTO valCursDTO = new ValCursDTO();
        ValuteDTO usdValuteDTO = createValuteDTO("USD", "95.00");
        valCursDTO.setValutes(List.of(usdValuteDTO));

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);
        when(cbrClient.getCurrencyRates()).thenReturn(valCursDTO);

        assertThrows(CurrencyRateNotFoundException.class, () -> currencyService.convertCurrency(requestDTO));
    }

    @Test
    void convertCurrency_InvalidToCurrency_ThrowsException() {
        CurrencyConvertRequestDTO requestDTO = createCurrencyConvertRequestDTO("USD", "INVALID", 100.0);

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        valuteFullDTO.setItems(List.of());

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);

        assertThrows(NonexistentCurrencyException.class, () -> currencyService.convertCurrency(requestDTO));
    }

    @Test
    void convertCurrency_ValidToCurrencyButRateNotFound_ThrowsException() {
        CurrencyConvertRequestDTO requestDTO = createCurrencyConvertRequestDTO("USD", "ATS", 100.0);

        ValuteFullDTO valuteFullDTO = new ValuteFullDTO();
        ItemDTO usdItemDTO = createItemDTO("USD");
        ItemDTO atsItemDTO = createItemDTO("ATS");
        valuteFullDTO.setItems(List.of(usdItemDTO, atsItemDTO));

        ValCursDTO valCursDTO = new ValCursDTO();
        ValuteDTO usdValuteDTO = createValuteDTO("USD", "95.00");
        valCursDTO.setValutes(List.of(usdValuteDTO));

        when(cbrClient.getCurrencies()).thenReturn(valuteFullDTO);
        when(cbrClient.getCurrencyRates()).thenReturn(valCursDTO);

        assertThrows(CurrencyRateNotFoundException.class, () -> currencyService.convertCurrency(requestDTO));
    }

    private ValuteDTO createValuteDTO(String charCode, String vUnitRate) {
        return ValuteDTO
                .builder()
                .charCode(charCode)
                .vUnitRate(vUnitRate)
                .build();
    }

    private ItemDTO createItemDTO(String isoCharCode) {

        return ItemDTO
                .builder()
                .isoCharCode(isoCharCode)
                .build();
    }

    private CurrencyConvertRequestDTO createCurrencyConvertRequestDTO(
            String fromCurrency, String toCurrency, double amount
    ) {

        return CurrencyConvertRequestDTO
                .builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .amount(amount).build();
    }
}

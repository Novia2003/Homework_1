package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private static final String RUSSIAN_RUBLE_CODE = "RUB";

    private final CBRClient cbrClient;

    public CurrencyRateResponseDTO getCurrencyRateByCode(String code) {
        if (!doesSuchCurrencyExist(code)) {
            throw new NonexistentCurrencyException(getNonexistentCurrencyExceptionMessage(code));
        }

        double rate = getCurrencyRateFromCBR(code);

        CurrencyRateResponseDTO dto = new CurrencyRateResponseDTO();
        dto.setCurrency(code);
        dto.setRate(rate);

        return dto;
    }

    private Double getCurrencyRateFromCBR(String code) {
        if (code.equals(RUSSIAN_RUBLE_CODE)) {
            return 1.;
        }

        ValCursDTO valCursDTO = cbrClient.getCurrencyRates();

        for (ValuteDTO valuteDTO : valCursDTO.getValutes()) {
            if (valuteDTO.getCharCode().equals(code)) {
                return getDoubleRate(valuteDTO.getVunitRate());
            }
        }

        throw new CurrencyRateNotFoundException("Rate for currency with code \"" + code + "\" not found");
    }

    public CurrencyConvertResponseDTO convertCurrency(CurrencyConvertRequestDTO requestDTO) {
        if (!(doesSuchCurrencyExist(requestDTO.getFromCurrency()))) {
            throw new NonexistentCurrencyException(
                    getNonexistentCurrencyExceptionMessage(requestDTO.getFromCurrency())
            );
        }

        if (!doesSuchCurrencyExist(requestDTO.getToCurrency())) {
            throw new NonexistentCurrencyException(
                    getNonexistentCurrencyExceptionMessage(requestDTO.getToCurrency())
            );
        }

        double rateFromCurrency = getCurrencyRateFromCBR(requestDTO.getFromCurrency());
        double rateToCurrency = getCurrencyRateFromCBR(requestDTO.getToCurrency());

        double convertedAmount = rateFromCurrency * requestDTO.getAmount() / rateToCurrency;

        CurrencyConvertResponseDTO responseDTO = new CurrencyConvertResponseDTO();
        responseDTO.setFromCurrency(requestDTO.getFromCurrency());
        responseDTO.setToCurrency(requestDTO.getToCurrency());
        responseDTO.setConvertedAmount(convertedAmount);

        return responseDTO;
    }

    private boolean doesSuchCurrencyExist(String code) {
        if (code.equals(RUSSIAN_RUBLE_CODE)) {
            return true;
        }

        ValuteFullDTO allCurrency = cbrClient.getCurrencies();

        for (ItemDTO itemDTO : allCurrency.getItems()) {
            if (itemDTO.getIsoCharCode().equals(code)) {
                return true;
            }
        }

        return false;
    }

    private Double getDoubleRate(String rate) {
        return Double.valueOf(rate.replace(',', '.'));
    }

    private String getNonexistentCurrencyExceptionMessage(String code) {
        return "Currency with code \"" + code + "\" doesn't exist";
    }
}

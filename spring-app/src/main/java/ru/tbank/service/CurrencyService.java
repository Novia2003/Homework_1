package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.dto.currency.CurrencyRateResponseDTO;
import ru.tbank.exception.CurrencyRateNotFoundException;
import ru.tbank.exception.NonexistentCurrencyException;
import ru.tbank.exception.ServiceUnavailableException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private static final String RATES_URL = "/rates/";

    private final RestTemplate currencyRestTemplate;

    public CurrencyRateResponseDTO getCurrencyRate(String code) {
        try {
            log.info("Requesting currency rate for code: {}", code);
            CurrencyRateResponseDTO response = currencyRestTemplate.getForObject(RATES_URL + code, CurrencyRateResponseDTO.class);
            log.info("Received currency rate for code {}: {}", code, response);

            return response;
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (HttpServerErrorException e) {
            handleServerError(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }

        return null;
    }

    private void handleClientError(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            log.error("Bad request: {}", e.getResponseBodyAsString());
            throw new NonexistentCurrencyException(e.getResponseBodyAsString());
        } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error("Currency rate not found: {}", e.getResponseBodyAsString());
            throw new CurrencyRateNotFoundException(e.getResponseBodyAsString());
        } else {
            log.error("Client error: {}", e.getMessage());
            throw e;
        }
    }

    private void handleServerError(HttpServerErrorException e) {
        if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            log.error("Service unavailable: {}", e.getResponseBodyAsString());
            throw new ServiceUnavailableException(e.getResponseBodyAsString());
        } else {
            log.error("Server error: {}", e.getMessage());
            throw e;
        }
    }

    private void handleGeneralError(Exception e) {
        log.error("General error: {}", e.getMessage());
        throw new RuntimeException("An unexpected error occurred", e);
    }
}

package ru.tbank.integration.http.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tbank.dto.cbr.curs.ValCursDTO;
import ru.tbank.dto.cbr.valute.ValuteFullDTO;
import ru.tbank.exception.ServiceUnavailableException;
import ru.tbank.integration.http.CBRClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CBRClientImpl implements CBRClient {

    private static final String DAILY_RATES_URL = "/scripts/XML_daily.asp";
    private static final String ALL_CURRENCIES = "/scripts/XML_valFull.asp";

    private final RestTemplate cbrRestTemplate;

    @CircuitBreaker(name = "external-system", fallbackMethod = "fallbackMethodRate")
    @Cacheable(value = "currencyRates", key = "#root.methodName")
    public ValCursDTO getCurrencyRates() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = "date_req=" + date.format(formatter);

        return cbrRestTemplate.getForObject(DAILY_RATES_URL + "?" + currentDate, ValCursDTO.class);
    }

    @CircuitBreaker(name = "external-system", fallbackMethod = "fallbackMethodCurrencies")
    @Cacheable(value = "currencyRates", key = "#root.methodName")
    public ValuteFullDTO getCurrencies() {
        return cbrRestTemplate.getForObject(ALL_CURRENCIES, ValuteFullDTO.class);
    }

    private ValCursDTO fallbackMethodRate(Throwable throwable) {
        throw new ServiceUnavailableException("Service Unavailable");
    }

    private ValuteFullDTO fallbackMethodCurrencies(Throwable throwable) {
        throw new ServiceUnavailableException("Service Unavailable");
    }
}

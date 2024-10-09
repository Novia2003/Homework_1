package ru.tbank.dto.currency;

import lombok.Data;

@Data
public class CurrencyConvertResponseDTO {
    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
}

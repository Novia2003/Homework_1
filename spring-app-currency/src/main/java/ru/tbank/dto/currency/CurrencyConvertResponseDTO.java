package ru.tbank.dto.currency;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConvertResponseDTO {
    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
}

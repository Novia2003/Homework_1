package ru.tbank.dto.currency;

import lombok.Data;

@Data
public class CurrencyRateResponseDTO {
    private String currency;
    private Double rate;
}
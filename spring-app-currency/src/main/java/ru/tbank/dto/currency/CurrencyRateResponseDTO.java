package ru.tbank.dto.currency;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyRateResponseDTO {
    private String currency;
    private Double rate;
}

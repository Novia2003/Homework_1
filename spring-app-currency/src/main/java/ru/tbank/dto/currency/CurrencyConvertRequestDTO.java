package ru.tbank.dto.currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CurrencyConvertRequestDTO {

    @NotBlank(message = "the fromCurrency field could not be blank")
    private String fromCurrency;

    @NotBlank(message = "the toCurrency field could not be blank")
    private String toCurrency;

    @NotNull(message = "the amount field must not be null")
    @Positive(message = "the amount field must be positive")
    private Double amount;
}

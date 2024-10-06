package ru.tbank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.currency.CurrencyConvertRequestDTO;
import ru.tbank.dto.currency.CurrencyConvertResponseDTO;
import ru.tbank.dto.currency.CurrencyRateResponseDTO;
import ru.tbank.service.CurrencyService;

@RestController
@RequestMapping("/currency")
@Validated
@RequiredArgsConstructor
@Tag(
        name = "CurrencyController",
        description = "Functions for viewing the exchange rate and converting one currency to another"
)
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("rates/{code}")
    @Operation(description = "Getting the currency exchange rate by its code")
    public CurrencyRateResponseDTO getCurrencyRateByRate(
            @NotBlank(message = "the \"code\" variable could not be blank")
            @PathVariable
            String code
    ) {
        return currencyService.getCurrencyRateByCode(code);
    }

    @PostMapping("/convert")
    @Operation(description = "Getting a list of user recipes")
    public CurrencyConvertResponseDTO convertCurrency(
            @RequestBody
            CurrencyConvertRequestDTO dto) {
        return currencyService.convertCurrency(dto);
    }
}

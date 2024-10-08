package ru.tbank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tbank.dto.currency.CurrencyConvertRequestDTO;
import ru.tbank.dto.currency.CurrencyConvertResponseDTO;
import ru.tbank.dto.currency.CurrencyRateResponseDTO;
import ru.tbank.dto.http.ErrorMessageResponse;
import ru.tbank.service.CurrencyService;

@RestController
@Validated
@RequestMapping("/currency")
@RequiredArgsConstructor
@Tag(
        name = "CurrencyController",
        description = "Functions for viewing the exchange rate and converting one currency to another"
)
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("rates/{code}")
    @Operation(
            description = "Getting the currency exchange rate by its code"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CurrencyRateResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Currency rate not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageResponse.class)
                            )
                    )
            }
    )
    public CurrencyRateResponseDTO getCurrencyRateByRate(
            @NotBlank(message = "the code variable could not be blank")
            @PathVariable
            String code
    ) {
        return currencyService.getCurrencyRateByCode(code);
    }

    @PostMapping("/convert")
    @Operation(
            description = "Getting a list of user recipes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CurrencyConvertResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Currency rate not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageResponse.class)
                            )
                    )
            }
    )
    public CurrencyConvertResponseDTO convertCurrency(
            @Valid
            @RequestBody
            CurrencyConvertRequestDTO dto) {
        return currencyService.convertCurrency(dto);
    }
}

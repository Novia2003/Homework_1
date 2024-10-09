package ru.tbank.integration.http.impl;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClientException;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tbank.dto.cbr.item.ItemDTO;
import ru.tbank.dto.cbr.valute.ValuteDTO;
import ru.tbank.exception.ServiceUnavailableException;
import ru.tbank.integration.http.CBRClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class CBRClientImplTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private CBRClient cbrClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance().build();

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CircuitBreakerRegistry customCircuitBreakerRegistry() {
            CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                    .slidingWindowSize(5)
                    .permittedNumberOfCallsInHalfOpenState(1)
                    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                    .minimumNumberOfCalls(1)
                    .waitDurationInOpenState(java.time.Duration.ofMillis(1000))
                    .failureRateThreshold(20)
                    .recordExceptions(IOException.class, TimeoutException.class, RestClientException.class)
                    .ignoreExceptions(IllegalStateException.class)
                    .build();

            return CircuitBreakerRegistry.of(config);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("rest.cbr.url", wireMock::baseUrl);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        wireMock.resetAll();
        circuitBreakerRegistry.circuitBreaker("cbr").reset();
    }

    @Test
    void getCurrencies_Successful() {
        wireMock.stubFor(
                WireMock.get(urlMatching("/scripts/XML_valFull.asp"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                                        .withBody(
                                                """
                                                        <Valuta name="Foreign Currency Market Lib">
                                                            <Item ID="R01010">
                                                                <Name>Австралийский доллар</Name>
                                                                <EngName>Australian Dollar</EngName>
                                                                <Nominal>1</Nominal>
                                                                <ParentCode>R01010</ParentCode>
                                                                <ISO_Num_Code>36</ISO_Num_Code>
                                                                <ISO_Char_Code>AUD</ISO_Char_Code>
                                                            </Item>
                                                            <Item ID="R01015">
                                                                <Name>Австрийский шиллинг</Name>
                                                                <EngName>Austrian Shilling</EngName>
                                                                <Nominal>1000</Nominal>
                                                                <ParentCode>R01015</ParentCode>
                                                                <ISO_Num_Code>40</ISO_Num_Code>
                                                                <ISO_Char_Code>ATS</ISO_Char_Code>
                                                            </Item>
                                                        </Valuta>
                                                """
                                        )
                        )
        );

        var result = cbrClient.getCurrencies();

        assertEquals(2, result.getItems().size());

        ItemDTO firstItemDTO = result.getItems().get(0);
        ItemDTO secondItemDTO = result.getItems().get(1);

        assertEquals("AUD", firstItemDTO.getIsoCharCode());
        assertEquals("ATS", secondItemDTO.getIsoCharCode());

    }

    @Test
    void getCurrencyRates_Successful() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDateRequestParam = "date_req=" + date.format(formatter);

        wireMock.stubFor(
                WireMock.get(urlEqualTo("/scripts/XML_daily.asp?" + currentDateRequestParam))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                                        .withBody(
                                                """
                                                        <ValCurs Date="08.10.2024" name="Foreign Currency Market">
                                                            <Valute ID="R01010">
                                                                <NumCode>036</NumCode>
                                                                <CharCode>AUD</CharCode>
                                                                <Nominal>1</Nominal>
                                                                <Name>Австралийский доллар</Name>
                                                                <Value>65,7852</Value>
                                                                <VunitRate>65,7852</VunitRate>
                                                            </Valute>
                                                            <Valute ID="R01020A">
                                                                <NumCode>944</NumCode>
                                                                <CharCode>AZN</CharCode>
                                                                <Nominal>1</Nominal>
                                                                <Name>Азербайджанский манат</Name>
                                                                <Value>56,5088</Value>
                                                                <VunitRate>56,5088</VunitRate>
                                                            </Valute>
                                                        </ValCurs>
                                                """
                                        )
                        )
        );

        var result = cbrClient.getCurrencyRates();

        assertEquals(2, result.getValutes().size());

        ValuteDTO firstValuteDTO = result.getValutes().get(0);
        ValuteDTO secondValuteDTO = result.getValutes().get(1);

        assertEquals("AUD", firstValuteDTO.getCharCode());
        assertEquals("65,7852", firstValuteDTO.getVUnitRate());
        assertEquals("AZN", secondValuteDTO.getCharCode());
        assertEquals("56,5088", secondValuteDTO.getVUnitRate());
    }

    @Test
    void getCurrencies_ServiceUnavailable() {
        wireMock.stubFor(
                WireMock.get(urlMatching("/scripts/XML_valFull.asp"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                                        .withStatus(500)
                        )
        );


        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class, () -> cbrClient.getCurrencies()
        );
        assertEquals("Service Unavailable. Message: 500 Server Error: [no body]", exception.getMessage());
    }

    @Test
    void getCurrencyRates_ServiceUnavailable() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDateRequestParam = "date_req=" + date.format(formatter);

        wireMock.stubFor(
                WireMock.get(urlEqualTo("/scripts/XML_daily.asp?" + currentDateRequestParam))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                                        .withStatus(500)
                        )
        );

        var exception = assertThrows(
                ServiceUnavailableException.class, () -> cbrClient.getCurrencyRates()
        );
        assertEquals("Service Unavailable. Message: 500 Server Error: [no body]", exception.getMessage());
    }
}
package ru.tbank.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class KudagoServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private KudagoService kudagoService;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(
                    wireMockConfig()
                            .dynamicPort()
                            .usingFilesUnderClasspath("wiremock")
            )
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("rest.kudago.url", wireMock::baseUrl);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testGetCategories() {
        var result = kudagoService.getCategories();

        assertEquals(5, result.length);

        assertEquals(123, result[0].getId());
        assertEquals("airports", result[0].getSlug());
        assertEquals("Аэропорты", result[0].getName());

        assertEquals(89, result[1].getId());
        assertEquals("amusement", result[1].getSlug());
        assertEquals("Развлечения", result[1].getName());

        assertEquals(114, result[2].getId());
        assertEquals("animal-shelters", result[2].getSlug());
        assertEquals("Питомники", result[2].getName());

        assertEquals(48, result[3].getId());
        assertEquals("theatre", result[3].getSlug());
        assertEquals("Театры", result[3].getName());

        assertEquals(127, result[4].getId());
        assertEquals("workshops", result[4].getSlug());
        assertEquals("Мастерские", result[4].getName());
    }

    @Test
    void testGetLocations() {
        var result = kudagoService.getLocations();

        assertEquals(5, result.length);

        assertEquals("ekb", result[0].getSlug());
        assertEquals("Екатеринбург", result[0].getName());

        assertEquals("kzn", result[1].getSlug());
        assertEquals("Казань", result[1].getName());

        assertEquals("msk", result[2].getSlug());
        assertEquals("Москва", result[2].getName());

        assertEquals("nnv", result[3].getSlug());
        assertEquals("Нижний Новгород", result[3].getName());

        assertEquals("spb", result[4].getSlug());
        assertEquals("Санкт-Петербург", result[4].getName());
    }

    @ParameterizedTest
    @CsvSource({
            "400", "500"
    })
    void testKudagoBadResponseCategories(int responseHttpCode) {
        wireMock.stubFor(
                WireMock.get(urlMatching("/place-categories"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withStatus(responseHttpCode)
                        )
        );

        var result = kudagoService.getCategories();

        assertNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            "400", "500"
    })
    void testKudagoBadResponseLocations(int responseHttpCode) {
        wireMock.stubFor(
                WireMock.get(urlMatching("/locations"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withStatus(responseHttpCode)
                        )
        );

        var result = kudagoService.getLocations();

        assertNull(result);
    }
}
package ru.tbank.configuration.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.tbank.configuration.property.rest.CurrencyRestProperties;

@Configuration
@RequiredArgsConstructor
public class CurrencyRestConfiguration {

    @Bean
    public RestTemplate currencyRestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            CurrencyRestProperties properties
    ) {
        return restTemplateBuilder
                .rootUri(properties.getUrl())
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .build();
    }
}

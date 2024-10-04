package ru.tbank.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.tbank.configuration.property.RestProperties;

@Configuration
@RequiredArgsConstructor
public class RestConfiguration {

    @Bean
    public RestTemplate kudagoRestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            RestProperties properties
    ) {
        return restTemplateBuilder
                .rootUri(properties.getUrl())
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .build();
    }
}

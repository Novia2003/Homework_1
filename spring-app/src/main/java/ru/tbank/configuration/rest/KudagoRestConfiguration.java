package ru.tbank.configuration.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.tbank.configuration.property.rest.KudagoRestProperties;

import java.util.concurrent.Semaphore;

@Configuration
@RequiredArgsConstructor
public class KudagoRestConfiguration {

    @Bean
    public RestTemplate kudagoRestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            KudagoRestProperties properties
    ) {
        return restTemplateBuilder
                .rootUri(properties.getUrl())
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .build();
    }

    @Bean
    public Semaphore kudagoSemaphore(
            KudagoRestProperties properties
    ) {
        return new Semaphore(properties.getMaximumNumberConcurrentRequests());
    }
}

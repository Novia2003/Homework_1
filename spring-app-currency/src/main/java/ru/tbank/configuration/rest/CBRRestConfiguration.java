package ru.tbank.configuration.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.tbank.configuration.property.rest.CBRRestProperties;

@Configuration
@RequiredArgsConstructor
public class CBRRestConfiguration {

    @Bean
    public RestTemplate cbrRestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            CBRRestProperties properties
    ) {
        return restTemplateBuilder
                .rootUri(properties.getUrl())
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .additionalMessageConverters(new MappingJackson2XmlHttpMessageConverter())
                .build();
    }
}

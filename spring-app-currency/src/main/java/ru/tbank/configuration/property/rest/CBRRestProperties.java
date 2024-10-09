package ru.tbank.configuration.property.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("rest.cbr")
public class CBRRestProperties {
    private String url;
    private Duration readTimeout;
    private Duration connectTimeout;
}

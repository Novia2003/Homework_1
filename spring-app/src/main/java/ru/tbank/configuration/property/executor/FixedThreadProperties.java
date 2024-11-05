package ru.tbank.configuration.property.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("fixed.thread.pool")
public class FixedThreadProperties {
    private int size;
}

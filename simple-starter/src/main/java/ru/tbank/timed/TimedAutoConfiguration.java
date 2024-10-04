package ru.tbank.timed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimedAutoConfiguration {

    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect();
    }
}

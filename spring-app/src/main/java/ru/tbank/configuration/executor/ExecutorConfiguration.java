package ru.tbank.configuration.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.configuration.property.executor.FixedThreadProperties;
import ru.tbank.configuration.property.executor.ScheduledThreadProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfiguration {

    private final FixedThreadProperties fixedThreadProperties;
    private final ScheduledThreadProperties scheduledThreadProperties;

    @Bean(name = "fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadProperties.getSize(), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("FixedThreadPool-" + thread.getId());

            return thread;
        });
    }

    @Bean(name = "scheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(scheduledThreadProperties.getSize(), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("ScheduledThreadPool-" + thread.getId());
            return thread;
        });
    }
}

package ru.tbank.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetric {

    private final Counter counter;

    public CustomMetric(MeterRegistry meterRegistry) {
        this.counter = meterRegistry.counter("custom_counter_metric");
    }

    public void increment() {
        counter.increment();
    }
}

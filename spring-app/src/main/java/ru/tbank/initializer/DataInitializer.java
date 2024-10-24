package ru.tbank.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.service.CategoryService;
import ru.tbank.service.KudagoService;
import ru.tbank.service.LocationService;
import ru.tbank.timed.Timed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final KudagoService kudagoService;

    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;

    @EventListener(ApplicationStartedEvent.class)
    @Timed
    public void init() {
        log.info("Starting data initialization...");

        scheduledThreadPool.schedule(this::initializeData, 0, TimeUnit.SECONDS);
    }

    @Scheduled(fixedDelayString = "${initialization.interval}")
    @Timed
    public void initializeData() {
        log.info("Scheduled data initialization started...");

        fixedThreadPool.submit(() -> {
            var categories = kudagoService.getCategories();
            if (categories == null) {
                log.info("No categories fetched. Received null response.");
            } else {
                log.info("Fetched {} categories.", categories.length);

                for (CategoryKudagoResponseDTO category : categories) {
                    categoryService.createCategory(category);
                }
            }
        });

        fixedThreadPool.submit(() -> {
            var locations = kudagoService.getLocations();
            if (locations == null) {
                log.info("No locations fetched. Received null response.");
            } else {
                log.info("Fetched {} locations.", locations.length);

                for (LocationKudagoResponseDTO location : locations) {
                    locationService.createLocation(location);
                }
            }
        });

        log.info("Scheduled data initialization completed.");
    }
}

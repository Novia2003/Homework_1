package ru.tbank.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.service.CategoryService;
import ru.tbank.service.KudagoService;
import ru.tbank.service.LocationService;
import ru.tbank.timed.Timed;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final KudagoService kudagoService;

    @EventListener(ApplicationReadyEvent.class)
    @Timed
    public void init() {
        log.info("Starting data initialization...");

        var categories = kudagoService.getCategories();
        if (categories == null) {
            log.info("No categories fetched. Received null response.");
        } else {
            log.info("Fetched {} categories.", categories.length);

            for (CategoryKudagoResponseDTO category : categories) {
                categoryService.createCategory(category);
            }
        }

        var locations = kudagoService.getLocations();
        if (locations == null) {
            log.info("No locations fetched. Received null response.");
        } else {
            log.info("Fetched {} locations.", locations.length);

            for (LocationKudagoResponseDTO location : locations) {
                locationService.createLocation(location);
            }
        }

        log.info("Data initialization completed.");
    }
}

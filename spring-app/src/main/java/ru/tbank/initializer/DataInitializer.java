package ru.tbank.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.service.CategoryService;
import ru.tbank.service.LocationService;
import ru.tbank.timed.Timed;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private static final String CATEGORIES_URL = "https://kudago.com/public-api/v1.4/place-categories";
    private static final String LOCATIONS_URL = "https://kudago.com/public-api/v1.4/locations";

    private final CategoryService categoryService;
    private final LocationService locationService;

    @EventListener(ApplicationReadyEvent.class)
    @Timed
    public void init() {
        log.info("Starting data initialization...");

        RestTemplate restTemplate = new RestTemplate();

        CategoryKudagoResponseDTO[] categories = restTemplate.getForObject(CATEGORIES_URL, CategoryKudagoResponseDTO[].class);
        if (categories == null) {
            log.error("Failed to fetch categories. Received null response.");
        } else {
            log.info("Fetched {} categories.", categories.length);

            for (CategoryKudagoResponseDTO category : categories) {
                categoryService.createCategory(category);
            }
        }

        LocationKudagoResponseDTO[] locations = restTemplate.getForObject(LOCATIONS_URL, LocationKudagoResponseDTO[].class);
        if (locations == null) {
            log.error("Failed to fetch locations. Received null response.");
        } else {
            log.info("Fetched {} locations.", locations.length);

            for (LocationKudagoResponseDTO location : locations) {
                locationService.createLocation(location);
            }
        }

        log.info("Data initialization completed.");
    }
}

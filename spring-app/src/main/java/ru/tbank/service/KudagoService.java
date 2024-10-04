package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class KudagoService {

    private static final String CATEGORIES_URL = "/place-categories";
    private static final String LOCATIONS_URL = "/locations";

    private final RestTemplate kudagoRestTemplate;

    public CategoryKudagoResponseDTO[] getCategories() {
        try {
            return kudagoRestTemplate.getForObject(CATEGORIES_URL, CategoryKudagoResponseDTO[].class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while fetching categories: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("Network error occurred while fetching categories: {}", e.getMessage());
        }

        return null;
    }

    public LocationKudagoResponseDTO[] getLocations() {
        try {
            return kudagoRestTemplate.getForObject(LOCATIONS_URL, LocationKudagoResponseDTO[].class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while fetching locations: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("Network error occurred while fetching locations: {}", e.getMessage());
        }

        return null;
    }
}

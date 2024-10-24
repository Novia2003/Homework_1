package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.event.kudago.EventKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;

import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class KudagoService {

    private static final String CATEGORIES_URL = "/place-categories";
    private static final String LOCATIONS_URL = "/locations";
    private static final String EVENTS_URL = "/events";

    private final RestTemplate kudagoRestTemplate;
    private final Semaphore kudagoSemaphore;

    public CategoryKudagoResponseDTO[] getCategories() {
        try {
            kudagoSemaphore.acquire();
            try {
                return kudagoRestTemplate.getForObject(CATEGORIES_URL, CategoryKudagoResponseDTO[].class);
            } finally {
                kudagoSemaphore.release();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while fetching categories: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("Network error occurred while fetching categories: {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Interrupted while acquiring semaphore: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        return null;
    }

    public LocationKudagoResponseDTO[] getLocations() {
        try {
            kudagoSemaphore.acquire();
            try {
                return kudagoRestTemplate.getForObject(LOCATIONS_URL, LocationKudagoResponseDTO[].class);
            } finally {
                kudagoSemaphore.release();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while fetching locations: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("Network error occurred while fetching locations: {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Interrupted while acquiring semaphore: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        return null;
    }

    public EventKudagoResponseDTO getEvents(long actualSince) {
        String actualSinceQueryParam = "actual_since=" + actualSince;
        String fieldsQueryParam = "fields=title,price,dates,location";

        try {
            kudagoSemaphore.acquire();
            try {
                return kudagoRestTemplate.getForObject(
                        EVENTS_URL + "?" + fieldsQueryParam + "&" + actualSinceQueryParam, EventKudagoResponseDTO.class
                );
            } finally {
                kudagoSemaphore.release();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while fetching events: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("Network error occurred while fetching events: {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Interrupted while acquiring semaphore: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        return null;
    }
}

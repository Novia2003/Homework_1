package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;

@Service
@RequiredArgsConstructor
public class KudagoService {

    private static final String CATEGORIES_URL = "/place-categories";
    private static final String LOCATIONS_URL = "/locations";

    private final RestTemplate kudagoRestTemplate;

    public CategoryKudagoResponseDTO[] getCategories() {
        return kudagoRestTemplate.getForObject(CATEGORIES_URL, CategoryKudagoResponseDTO[].class);
    }

    public LocationKudagoResponseDTO[] getLocations() {
        return kudagoRestTemplate.getForObject(LOCATIONS_URL, LocationKudagoResponseDTO[].class);
    }
}

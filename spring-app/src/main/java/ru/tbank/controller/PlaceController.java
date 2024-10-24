package ru.tbank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.dto.place.PlaceDTO;
import ru.tbank.dto.place.PlaceDetailsDTO;
import ru.tbank.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public List<PlaceDTO> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/{id}")
    public PlaceDTO getPlaceById(@PathVariable Long id) {
        return placeService.getPlaceById(id);
    }

    @PostMapping
    public PlaceDTO createPlace(
            @Valid
            @RequestBody
            PlaceDetailsDTO placeDetails
    ) {
        return placeService.createPlace(placeDetails);
    }

    @PutMapping("/{id}")
    public PlaceDTO updatePlace(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            PlaceDetailsDTO placeDetails
    ) {
        return placeService.updatePlace(id, placeDetails);
    }

    @DeleteMapping("/{id}")
    public void deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
    }
}

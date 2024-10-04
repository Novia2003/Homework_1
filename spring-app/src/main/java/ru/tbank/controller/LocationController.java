package ru.tbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.dto.location.LocationDTO;
import ru.tbank.service.LocationService;
import ru.tbank.timed.Timed;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Timed
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public Collection<LocationDTO> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        LocationDTO location = locationService.getLocationById(id);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
    }

    @PostMapping
    public Long createLocation(@RequestBody LocationDTO locationDTO) {
        return locationService.createLocation(locationDTO);
    }

    @PutMapping("/{id}")
    public void updateLocation(@PathVariable Long id, @RequestBody LocationDTO locationDTO) {
        locationService.updateLocation(id, locationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
    }
}

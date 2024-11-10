package ru.tbank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.location.LocationDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.model.Location;
import ru.tbank.repository.CustomRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private CustomRepository<Location> repository;

    @InjectMocks
    private LocationService locationService;

    @Test
    void testGetAllLocations() {
        Location moscowLocation = createLocation("Москва", "msk");
        Location kazanLocation = createLocation("Екатеринбург", "ekb");
        when(repository.findAll()).thenReturn(Arrays.asList(moscowLocation, kazanLocation));

        Collection<LocationDTO> result = locationService.getAllLocations();

        Iterator<LocationDTO> iterator = result.iterator();
        String firstElementName = iterator.next().getName();
        assertEquals("Москва", firstElementName);

        String secondElementSlug = iterator.next().getSlug();
        assertEquals("ekb", secondElementSlug);
    }

    @Test
    void testGetLocationById() {
        Location moscowLocation = createLocation("Москва", "msk");
        when(repository.findById(1L)).thenReturn(moscowLocation);

        LocationDTO result = locationService.getLocationById(1L);

        assertEquals("Москва", result.getName());
    }

    @Test
    void testCreateLocation() {
        LocationDTO dto = createLocationDTO("Москва", "msk");
        when(repository.save(any(Location.class))).thenReturn(1L);

        Long result = locationService.createLocation(dto);

        assertEquals(1L, result);
    }

    @Test
    void testCreateLocationDuringInitialize() {
        LocationKudagoResponseDTO dto = new LocationKudagoResponseDTO();
        dto.setSlug("msk");
        dto.setName("Москва");

        locationService.createLocation(dto);

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(repository).update(locationCaptor.capture());

        List<Location> capturedLocations = locationCaptor.getAllValues();
        assertEquals(1, capturedLocations.size());

        Location capturedLocation = capturedLocations.get(0);
        assertEquals("msk", capturedLocation.getSlug());
        assertEquals("Москва", capturedLocation.getName());
    }

    @Test
    void testUpdateLocationById() {
        LocationDTO dto = createLocationDTO("Москва", "msk");

        locationService.updateLocation(1L, dto);

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(repository).update(eq(1L), locationCaptor.capture());

        List<Location> capturedLocations = locationCaptor.getAllValues();
        assertEquals(1, capturedLocations.size());

        Location capturedLocation = capturedLocations.get(0);
        assertEquals("Москва", capturedLocation.getName());
        assertEquals("msk", capturedLocation.getSlug());
    }

    @Test
    void testDeleteLocationById() {
        locationService.deleteLocation(1L);

        verify(repository).delete(1L);
    }

    @Test
    void testGetLocationByNonExistentId() {
        when(repository.findById(1L)).thenReturn(null);

        LocationDTO result = locationService.getLocationById(1L);

        assertNull(result);
    }

    @Test
    void testUpdateLocationByNonExistentId() {
        LocationDTO dto = createLocationDTO("Москва", "msk");
        doThrow(new IllegalArgumentException()).when(repository).update(eq(1L), any(Location.class));

        assertThrows(IllegalArgumentException.class, () -> locationService.updateLocation(1L, dto));
    }

    @Test
    void testDeleteLocationByNonExistentId() {
        doThrow(new IllegalArgumentException()).when(repository).delete(1L);

        assertThrows(IllegalArgumentException.class, () -> locationService.deleteLocation(1L));
    }

    private Location createLocation(String name, String slug) {
        Location location = new Location();
        location.setName(name);
        location.setSlug(slug);

        return location;
    }

    private LocationDTO createLocationDTO(String name, String slug) {
        LocationDTO dto = new LocationDTO();
        dto.setName(name);
        dto.setSlug(slug);

        return dto;
    }
}

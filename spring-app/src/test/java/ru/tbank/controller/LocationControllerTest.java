package ru.tbank.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.dto.location.LocationDTO;
import ru.tbank.service.LocationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({LocationController.class})
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    public void getAllLocations() throws Exception {
        List<LocationDTO> locations = Arrays.asList(
                createLocationDTO("Москва", "msk"),
                createLocationDTO("Екатеринбург", "ekb")
        );

        when(locationService.getAllLocations()).thenReturn(locations);

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                [
                                {
                                    "slug": "msk",
                                    "name": "Москва"
                                },
                                {
                                    "slug": "ekb",
                                    "name": "Екатеринбург"
                                }
                                ]
                                """
                ));
    }

    @Test
    public void getLocationById() throws Exception {
        LocationDTO location = createLocationDTO("Москва", "msk");

        when(locationService.getLocationById(anyLong())).thenReturn(location);

        mockMvc.perform(get("/api/v1/locations/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                {
                                    "slug": "msk",
                                    "name": "Москва"
                                }
                                """
                ));
    }

    @Test
    public void getLocationByNonExistentId() throws Exception {
        when(locationService.getLocationById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/v1/locations/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createLocation() throws Exception {
        String locationDTO = """
                {
                    "slug": "msk",
                    "name": "Москва"
                }
                """;

        when(locationService.createLocation(any(LocationDTO.class))).thenReturn(1L);

        mockMvc.perform(post("/api/v1/locations")
                        .content(locationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void updateLocationById() throws Exception {
        String locationDTO = """
                {
                    "slug": "msk",
                    "name": "Москва"
                }
                """;

        doNothing().when(locationService).updateLocation(anyLong(), any(LocationDTO.class));

        mockMvc.perform(put("/api/v1/locations/{id}", 1)
                        .content(locationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteLocationById() throws Exception {
        doNothing().when(locationService).deleteLocation(anyLong());

        mockMvc.perform(delete("/api/v1/locations/{id}", 1))
                .andExpect(status().isOk());
    }

    private LocationDTO createLocationDTO(String name, String slug) {
        LocationDTO dto = new LocationDTO();
        dto.setName(name);
        dto.setSlug(slug);

        return dto;
    }
}

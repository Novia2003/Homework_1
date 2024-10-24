package ru.tbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.SpringAppApplicationTest;
import ru.tbank.dto.place.PlaceDTO;
import ru.tbank.dto.place.PlaceDetailsDTO;
import ru.tbank.entity.PlaceEntity;
import ru.tbank.repository.PlaceRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlaceControllerTest extends SpringAppApplicationTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
    public void testCreatePlace() throws Exception {
        String name = "Moscow";

        PlaceDetailsDTO placeDetailsDTO = new PlaceDetailsDTO();
        placeDetailsDTO.setName(name);

        mockMvc.perform(post("/api/v1/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        PlaceEntity savedPlace = placeRepository.findAll().get(0);
        assertNotNull(savedPlace);
        assertEquals(name, savedPlace.getName());
    }

    @Test
    @DirtiesContext
    public void testGetPlaceById() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Voronezh");
        placeRepository.save(place);

        PlaceEntity savedPlace = placeRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/places/" + savedPlace.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        PlaceDTO
                                .builder()
                                .id(place.getId())
                                .name(place.getName())
                                .build()
                )));
    }

    @Test
    @DirtiesContext
    public void testUpdatePlace() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Piter");
        placeRepository.save(place);

        PlaceEntity savedPlace = placeRepository.findAll().get(0);
        String updatedName = "Saint-Petersburg";

        PlaceDetailsDTO placeDetailsDTO = new PlaceDetailsDTO();
        placeDetailsDTO.setName(updatedName);

        mockMvc.perform(put("/api/v1/places/" + savedPlace.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        PlaceEntity updatedPlace = placeRepository.findById(savedPlace.getId()).get();
        assertEquals(updatedName, updatedPlace.getName());
    }
}
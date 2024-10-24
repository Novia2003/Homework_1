package ru.tbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.SpringAppApplicationTest;
import ru.tbank.dto.event.EventDTO;
import ru.tbank.dto.event.EventDetailsDTO;
import ru.tbank.entity.EventEntity;
import ru.tbank.entity.PlaceEntity;
import ru.tbank.repository.EventRepository;
import ru.tbank.repository.PlaceRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends SpringAppApplicationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
    public void testCreateEvent() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Moscow");
        placeRepository.save(place);

        String eventName = "Party";
        LocalDate date = LocalDate.now();

        EventDetailsDTO event = new EventDetailsDTO();
        event.setName(eventName);
        event.setDate(date);
        event.setPlaceId(place.getId());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        EventEntity createdEvent = eventRepository.findAll().get(0);
        assertNotNull(createdEvent);
        assertEquals(eventName, createdEvent.getName());
        assertEquals(date, createdEvent.getDate());
        assertEquals(place.getId(), createdEvent.getPlace().getId());
    }

    @Test
    @DirtiesContext
    public void testGetEventById() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Voronezh");
        placeRepository.save(place);

        EventEntity event = new EventEntity();
        event.setName("Birthday");
        event.setDate(LocalDate.now());
        event.setPlace(place);
        eventRepository.save(event);

        EventEntity savedEvent = eventRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/events/" + savedEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        EventDTO
                                .builder()
                                .id(savedEvent.getId())
                                .name(event.getName())
                                .date(event.getDate())
                                .placeId(place.getId())
                                .build()
                )));
    }

    @Test
    @DirtiesContext
    public void testUpdateEvent() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Samara");
        placeRepository.save(place);
        LocalDate date = LocalDate.now();

        EventEntity event = new EventEntity();
        event.setName("Exhibition");
        event.setDate(date);
        event.setPlace(place);
        eventRepository.save(event);

        EventEntity savedEvent = eventRepository.findAll().get(0);
        String updatedName = "Show";

        EventDetailsDTO eventDetailsDTO = new EventDetailsDTO();
        eventDetailsDTO.setName(updatedName);
        eventDetailsDTO.setDate(savedEvent.getDate());
        eventDetailsDTO.setPlaceId(savedEvent.getPlace().getId());

        mockMvc.perform(put("/api/v1/events/" + savedEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        EventEntity updatedEvent = eventRepository.findById(savedEvent.getId()).get();
        assertEquals(updatedName, updatedEvent.getName());
        assertEquals(date, updatedEvent.getDate());
        assertEquals(place.getId(), updatedEvent.getPlace().getId());
    }
}
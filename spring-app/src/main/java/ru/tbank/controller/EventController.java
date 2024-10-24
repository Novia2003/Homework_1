package ru.tbank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tbank.dto.event.EventDTO;
import ru.tbank.dto.event.EventDetailsDTO;
import ru.tbank.dto.event.EventRequestDTO;
import ru.tbank.dto.event.EventResponseDTO;
import ru.tbank.service.EventService;
import ru.tbank.timed.Timed;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Timed
public class EventController {

    private final EventService eventService;

    @GetMapping("/byCompletableFuture")
    public CompletableFuture<ResponseEntity<List<EventResponseDTO>>> getEventsByCompletableFuture(
            @RequestParam double budget,
            @RequestParam String currency,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {

        EventRequestDTO request = new EventRequestDTO();
        request.setBudget(budget);
        request.setCurrency(currency);

        if (dateFrom != null) {
            request.setDateFrom(LocalDate.parse(dateFrom));
        }

        if (dateTo != null) {
            request.setDateTo(LocalDate.parse(dateTo));
        }

        return eventService.getEventsByCompletableFuture(request).thenApply(ResponseEntity::ok);
    }

    @GetMapping("/byMonoAndFlux")
    public Mono<ResponseEntity<Flux<EventResponseDTO>>> getEventsByMonoAndFlux(
            @RequestParam double budget,
            @RequestParam String currency,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {

        EventRequestDTO request = new EventRequestDTO();
        request.setBudget(budget);
        request.setCurrency(currency);

        if (dateFrom != null) {
            request.setDateFrom(LocalDate.parse(dateFrom));
        }

        if (dateTo != null) {
            request.setDateTo(LocalDate.parse(dateTo));
        }

        Flux<EventResponseDTO> eventsFlux = eventService.getEventsByMonoAndFlux(request);

        return Mono.just(ResponseEntity.ok(eventsFlux));
    }

    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/filter")
    public List<EventDTO> getEvents(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "place", required = false) String placeName,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
    ) {
        return eventService.getEvents(name, placeName, fromDate, toDate);
    }

    @PostMapping
    public EventDTO createEvent(
            @Valid
            @RequestBody
            EventDetailsDTO eventDetails
    ) {
        return eventService.createEvent(eventDetails);
    }

    @PutMapping("/{id}")
    public EventDTO updateEvent(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            EventDetailsDTO eventDetails
    ) {
        return eventService.updateEvent(id, eventDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}

package ru.tbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
}

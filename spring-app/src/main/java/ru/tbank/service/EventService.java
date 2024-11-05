package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tbank.dto.currency.CurrencyRateResponseDTO;
import ru.tbank.dto.event.EventDTO;
import ru.tbank.dto.event.EventDetailsDTO;
import ru.tbank.dto.event.EventRequestDTO;
import ru.tbank.dto.event.EventResponseDTO;
import ru.tbank.dto.event.kudago.EventKudagoDatesDTO;
import ru.tbank.dto.event.kudago.EventKudagoResultDTO;
import ru.tbank.entity.EventEntity;
import ru.tbank.entity.PlaceEntity;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.exception.RelatedEntityNotFoundException;
import ru.tbank.repository.EventRepository;
import ru.tbank.repository.PlaceRepository;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final KudagoService kudagoService;

    private final CurrencyService currencyService;

    private final EventRepository eventRepository;

    private final PlaceRepository placeRepository;

    public CompletableFuture<List<EventResponseDTO>> getEventsByCompletableFuture(EventRequestDTO request) {
        LocalDate dateFrom = Optional.ofNullable(request.getDateFrom())
                .orElseGet(() -> LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

        LocalDate dateTo = Optional.ofNullable(request.getDateTo())
                .orElseGet(() -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));


        long dateFromTimestamp = dateFrom.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long dateToTimestamp = dateTo.atStartOfDay().plusDays(1).toEpochSecond(ZoneOffset.UTC);

        CompletableFuture<List<EventKudagoResultDTO>> eventsFuture = CompletableFuture.supplyAsync(
                () -> kudagoService.getEvents(dateFromTimestamp).getResults()
        );
        CompletableFuture<Double> budgetInRublesFuture = CompletableFuture.supplyAsync(
                () -> convertBudgetToRubles(request.getBudget(), request.getCurrency())
        );

        return eventsFuture.thenCombine(budgetInRublesFuture, (events, budgetInRubles) ->
                events.stream()
                        .filter(event -> checkDateForComplianceWithInterval(
                                event.getDates(),
                                dateFromTimestamp,
                                dateToTimestamp
                        ))
                        .peek(event -> removeUnnecessaryEventDates(event, dateFromTimestamp, dateToTimestamp))
                        .filter(event -> findMinPrice(event.getPrice()) <= budgetInRubles)
                        .map(this::eventKudagoResultDTOToEventResponseDTO)
                        .collect(Collectors.toList())
        );
    }

    public Flux<EventResponseDTO> getEventsByMonoAndFlux(EventRequestDTO request) {
        LocalDate dateFrom = Optional.ofNullable(request.getDateFrom())
                .orElseGet(() -> LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

        LocalDate dateTo = Optional.ofNullable(request.getDateTo())
                .orElseGet(() -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));

        long dateFromTimestamp = dateFrom.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long dateToTimestamp = dateTo.atStartOfDay().plusDays(1).toEpochSecond(ZoneOffset.UTC);

        Mono<List<EventKudagoResultDTO>> eventsMono = Mono.fromCallable(() -> kudagoService.getEvents(dateFromTimestamp).getResults());
        Mono<Double> budgetInRublesMono = Mono.fromCallable(() -> convertBudgetToRubles(request.getBudget(), request.getCurrency()));

        return Mono.zip(eventsMono, budgetInRublesMono)
                .flatMapMany(tuple -> {
                    List<EventKudagoResultDTO> events = tuple.getT1();
                    Double budgetInRubles = tuple.getT2();

                    return Flux.fromIterable(events)
                            .filter(event -> checkDateForComplianceWithInterval(
                                    event.getDates(),
                                    dateFromTimestamp,
                                    dateToTimestamp
                            ))
                            .doOnNext(event -> removeUnnecessaryEventDates(event, dateFromTimestamp, dateToTimestamp))
                            .filter(event -> findMinPrice(event.getPrice()) <= budgetInRubles)
                            .map(this::eventKudagoResultDTOToEventResponseDTO);
                });
    }

    private Double convertBudgetToRubles(double budget, String currency) {
        return budget * getExchangeRate(currency.toUpperCase());
    }

    private double getExchangeRate(String currencyCode) {
        CurrencyRateResponseDTO dto = currencyService.getCurrencyRate(currencyCode);
        return dto.getRate();
    }

    private double findMinPrice(String text) {
        String patternString = "\\d*\\.?\\d+";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);

        double minimumPrice = 0;

        if (matcher.find()) {
            minimumPrice = Double.parseDouble(matcher.group());
        }

        return minimumPrice;
    }

    private boolean checkDateForComplianceWithInterval(
            List<EventKudagoDatesDTO> dates, long dateFromTimestamp, long dateToTimestamp
    ) {
        for (EventKudagoDatesDTO dto : dates) {
            if (dto.getStart() >= dateFromTimestamp && dto.getEnd() <= dateToTimestamp) {
                return true;
            }
        }

        return false;
    }

    private void removeUnnecessaryEventDates(
            EventKudagoResultDTO responseDTO, long dateFromTimestamp, long dateToTimestamp
    ) {
        List<EventKudagoDatesDTO> suitableDate = new ArrayList<>();

        for (EventKudagoDatesDTO dto : responseDTO.getDates()) {
            if (dto.getStart() >= dateFromTimestamp && dto.getEnd() <= dateToTimestamp) {
                suitableDate.add(dto);
                responseDTO.setDates(suitableDate);
                return;
            }
        }
    }

    private EventResponseDTO eventKudagoResultDTOToEventResponseDTO(EventKudagoResultDTO kudagoResultDTO) {
        EventResponseDTO responseDTO = new EventResponseDTO();
        responseDTO.setTitle(kudagoResultDTO.getTitle());
        responseDTO.setStartDate(
                LocalDateTime.ofInstant(Instant.ofEpochSecond(kudagoResultDTO.getDates().get(0).getStart()), ZoneOffset.UTC)
        );
        responseDTO.setEndDate(
                LocalDateTime.ofInstant(Instant.ofEpochSecond(kudagoResultDTO.getDates().get(0).getEnd()), ZoneOffset.UTC)
        );
        responseDTO.setPrice(findMinPrice(kudagoResultDTO.getPrice()));

        return responseDTO;
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository
                .findAll()
                .stream()
                .map(EventDTO::of)
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id) {
        return eventRepository
                .findById(id)
                .map(EventDTO::of)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public EventDTO createEvent(EventDetailsDTO eventDetails) {
        PlaceEntity place = placeRepository.findById(eventDetails.getPlaceId()).orElseThrow(
                () -> new RelatedEntityNotFoundException("Place not found")
        );

        EventEntity event = new EventEntity();
        event.setName(eventDetails.getName());
        event.setDate(eventDetails.getDate());
        event.setPlace(place);
        eventRepository.save(event);

        return EventDTO.of(event);
    }

    public EventDTO updateEvent(Long id, EventDetailsDTO eventDetails) {
        EventEntity event = eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Event not found")
        );

        PlaceEntity place = placeRepository.findById(eventDetails.getPlaceId()).orElseThrow(
                () -> new RelatedEntityNotFoundException("Place not found")
        );

        event.setName(eventDetails.getName());
        event.setDate(eventDetails.getDate());
        event.setPlace(place);
        eventRepository.save(event);

        return EventDTO.of(event);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found");
        }

        eventRepository.deleteById(id);
    }

    public List<EventDTO> getEvents(String name, String placeName, LocalDate fromDate, LocalDate toDate) {
        return eventRepository
                .findAll(EventRepository.buildSpecification(name, placeName, fromDate, toDate))
                .stream()
                .map(EventDTO::of)
                .collect(Collectors.toList());
    }
}

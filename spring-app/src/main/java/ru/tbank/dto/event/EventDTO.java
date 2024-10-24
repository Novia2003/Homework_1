package ru.tbank.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.tbank.entity.EventEntity;

import java.time.LocalDate;

@Data
@Builder
public class EventDTO {

    private Long id;

    private String name;

    private LocalDate date;

    private Long placeId;

    public static EventDTO of(EventEntity event) {
        return EventDTO
                .builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getDate())
                .placeId(event.getPlace().getId())
                .build();
    }
}

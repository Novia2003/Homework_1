package ru.tbank.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventDetailsDTO {

    @NotNull
    private String name;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long placeId;
}

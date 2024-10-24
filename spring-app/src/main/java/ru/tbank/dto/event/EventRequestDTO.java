package ru.tbank.dto.event;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventRequestDTO {
    private double budget;
    private String currency;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}

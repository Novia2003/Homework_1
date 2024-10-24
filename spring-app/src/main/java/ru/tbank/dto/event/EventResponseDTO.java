package ru.tbank.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponseDTO {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double price;
}
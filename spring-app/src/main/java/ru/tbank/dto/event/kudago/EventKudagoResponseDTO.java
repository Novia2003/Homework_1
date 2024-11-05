package ru.tbank.dto.event.kudago;

import lombok.Data;

import java.util.List;

@Data
public class EventKudagoResponseDTO {
    private long count;
    private String next;
    private String previous;
    private List<EventKudagoResultDTO> results;
}

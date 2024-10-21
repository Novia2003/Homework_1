package ru.tbank.dto.event.kudago;

import lombok.Data;

import java.util.List;

@Data
public class EventKudagoResultDTO {
    private List<EventKudagoDatesDTO> dates;
    private String title;
    private EventKudagoLocationDTO location;
    private String price;
}

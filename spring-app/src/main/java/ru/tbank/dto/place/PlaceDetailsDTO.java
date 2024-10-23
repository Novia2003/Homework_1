package ru.tbank.dto.place;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceDetailsDTO {

    @NotNull
    private String name;
}

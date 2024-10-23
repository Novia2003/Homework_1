package ru.tbank.dto.place;

import lombok.Builder;
import lombok.Data;
import ru.tbank.entity.PlaceEntity;

@Data
@Builder
public class PlaceDTO {

    private Long id;

    private String name;

    public static PlaceDTO of(PlaceEntity place) {
        return PlaceDTO
                .builder()
                .id(place.getId())
                .name(place.getName())
                .build();
    }
}

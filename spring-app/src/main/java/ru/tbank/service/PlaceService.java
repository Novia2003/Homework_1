package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.dto.place.PlaceDetailsDTO;
import ru.tbank.dto.place.PlaceDTO;
import ru.tbank.entity.PlaceEntity;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.repository.PlaceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<PlaceDTO> getAllPlaces() {
        return placeRepository
                .findAll()
                .stream()
                .map(PlaceDTO::of)
                .collect(Collectors.toList());
    }


    public PlaceDTO getPlaceById(Long id) {
        return placeRepository
                .findByIdWithEvents(id)
                .map(PlaceDTO::of)
                .orElseThrow(() -> new EntityNotFoundException("Place not found")
                );
    }

    public PlaceDTO createPlace(PlaceDetailsDTO place) {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName(place.getName());
        placeRepository.save(placeEntity);

        return PlaceDTO.of(placeEntity);
    }

    public PlaceDTO updatePlace(Long id, PlaceDetailsDTO placeDetails) {
        PlaceEntity place = placeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Place not found")
        );

        place.setName(placeDetails.getName());
        placeRepository.save(place);

        return PlaceDTO.of(place);
    }

    public void deletePlace(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new EntityNotFoundException("Place not found");
        }

        placeRepository.deleteById(id);
    }
}

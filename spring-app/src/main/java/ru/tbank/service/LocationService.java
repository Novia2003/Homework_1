package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.dto.location.LocationDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.model.Location;
import ru.tbank.repository.CustomRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final CustomRepository<Location> repository;

    public Collection<LocationDTO> getAllLocations() {
        return repository
                .findAll()
                .stream()
                .map(this::parseModelToDTO)
                .collect(Collectors.toList());
    }

    public LocationDTO getLocationById(Long id) {
        return parseModelToDTO(repository.findById(id));
    }

    public Long createLocation(LocationDTO dto) {
        return repository.save(parseDTOToModel(dto));
    }

    public void createLocation(LocationKudagoResponseDTO dto) {
        repository.save(parseKudagoResponseDTOToModel(dto));
    }

    public void updateLocation(Long id, LocationDTO dto) {
        repository.update(id, parseDTOToModel(dto));
    }

    public void deleteLocation(Long id) {
        repository.delete(id);
    }

    private LocationDTO parseModelToDTO(Location category) {
        LocationDTO dto = new LocationDTO();
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());

        return dto;
    }

    private Location parseDTOToModel(LocationDTO dto) {
        Location location = new Location();
        location.setSlug(dto.getSlug());
        location.setName(dto.getName());

        return location;
    }

    private Location parseKudagoResponseDTOToModel(LocationKudagoResponseDTO dto) {
        Location location = new Location();
        location.setSlug(dto.getSlug());
        location.setName(dto.getName());

        return location;
    }
}

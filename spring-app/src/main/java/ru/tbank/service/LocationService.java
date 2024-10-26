package ru.tbank.service;

import org.springframework.stereotype.Service;
import ru.tbank.dto.location.LocationDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.model.Location;
import ru.tbank.pattern.observer.impl.ObservableImpl;
import ru.tbank.repository.CustomRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class LocationService extends ObservableImpl<Location> {

    private final CustomRepository<Location> repository;

    public LocationService(CustomRepository<Location> repository) {
        this.repository = repository;
        addObserver(repository);
    }

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
        notifyObservers(parseKudagoResponseDTOToModel(dto));
    }

    public void updateLocation(Long id, LocationDTO dto) {
        repository.update(id, parseDTOToModel(dto));
    }

    public void deleteLocation(Long id) {
        repository.delete(id);
    }

    public void restore() {
        repository.restore();
    }

    private LocationDTO parseModelToDTO(Location location) {
        if (location == null) {
            return null;
        }

        LocationDTO dto = new LocationDTO();
        dto.setName(location.getName());
        dto.setSlug(location.getSlug());

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

package ru.tbank.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.pattern.observer.command.Command;
import ru.tbank.service.KudagoService;
import ru.tbank.service.LocationService;

@Slf4j
@RequiredArgsConstructor
public class FetchAndSaveLocationsCommand implements Command {

    private final KudagoService kudagoService;
    private final LocationService locationService;

    @Override
    public void execute() {
        var locations = kudagoService.getLocations();

        if (locations == null) {
            log.info("No locations fetched. Received null response.");
        } else {
            log.info("Fetched {} locations.", locations.length);

            for (LocationKudagoResponseDTO location : locations) {
                locationService.createLocation(location);
            }
        }
    }
}

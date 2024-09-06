package org.example.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.City;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JsonParser {

    public static City parseCity(String path) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            log.info("Parsing file: {}", path);
            City city = mapper.readValue(new File(path), City.class);
            log.debug("Parsed city object: {}", city);

            return city;
        } catch (IOException e) {
            log.error("Error parsing file: {}", path, e);

            return null;
        }
    }

}

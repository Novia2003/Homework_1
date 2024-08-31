package org.example.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class JsonParser {

    private static final Logger logger = LoggerFactory.getLogger(JsonParser.class);

    public static City parseCity(String path) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            logger.info("Parsing file: {}", path);
            City city = mapper.readValue(new File(path), City.class);
            logger.info("Successfully parsed city: {}", city);

            return city;
        } catch (IOException e) {
            logger.error("Error parsing file: {}", path, e);

            return null;
        }
    }

}

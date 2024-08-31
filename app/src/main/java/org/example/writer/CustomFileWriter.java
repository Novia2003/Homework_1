package org.example.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

public class CustomFileWriter {

    private static final Logger logger = LoggerFactory.getLogger(CustomFileWriter.class);

    public static void writeToFile(String text, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            logger.info("Writing XML to file: {}", path);
            writer.write(text);
            logger.info("Successfully wrote XML to file: {}", path);
        } catch (IOException e) {
            logger.error("Error writing XML to file: {}", path, e);
        }
    }

}

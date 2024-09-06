package org.example.writer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class CustomFileWriter {

    public static void writeToFile(String text, String path) {
        if (path == null || path.isEmpty()) {
            log.warn("File path is null or empty.");
            return;
        }

        try (FileWriter writer = new FileWriter(path)) {
            log.info("Writing XML to file: {}", path);
            writer.write(text);
            log.debug("Wrote XML to file: {}", path);
        } catch (IOException e) {
            log.error("Error writing XML to file: {}", path, e);
        }
    }

}

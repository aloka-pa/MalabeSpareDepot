package com.malabe.sparedepot.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuditLogger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private final Path filePath;

    public AuditLogger(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public void log(String action, String itemCode, String quantityText) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath.toFile(), true));
            writer.write(LocalDateTime.now().format(FORMATTER) + " | " + action + " | " + itemCode + " | " + quantityText);
            writer.newLine();
        } catch (IOException ex) {
            // ignore logging failure
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }
}

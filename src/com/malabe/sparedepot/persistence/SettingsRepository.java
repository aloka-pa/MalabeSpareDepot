package com.malabe.sparedepot.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsRepository {
    private final Path filePath;

    public SettingsRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public int loadLowStockThreshold() {
        if (!Files.exists(filePath)) {
            return 10;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath.toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("lowStockThreshold=")) {
                    String value = trimmed.substring("lowStockThreshold=".length()).trim();
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        return 10;
                    }
                }
            }
        } catch (IOException ex) {
            return 10;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
        return 10;
    }

    public void saveLowStockThreshold(int threshold) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false));
        try {
            writer.write("lowStockThreshold=" + threshold);
            writer.newLine();
        } finally {
            writer.close();
        }
    }
}

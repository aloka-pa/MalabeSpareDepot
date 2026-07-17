package com.malabe.sparedepot.persistence;

import com.malabe.sparedepot.model.Part;
import com.malabe.sparedepot.parser.DirtyLegacyParser;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    private final Path filePath;

    public InventoryRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public List<Part> load() {
        List<Part> parts = new ArrayList<Part>();
        if (!Files.exists(filePath)) {
            return parts;
        }
        try {
            parts = DirtyLegacyParser.parseParts(new FileReader(filePath.toFile()));
        } catch (IOException ex) {
            return new ArrayList<Part>();
        }
        return parts;
    }

    public void save(List<Part> parts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false));
        try {
            for (int i = 0; i < parts.size(); i++) {
                writer.write(DirtyLegacyParser.toCanonicalPartLine(parts.get(i)));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }
}

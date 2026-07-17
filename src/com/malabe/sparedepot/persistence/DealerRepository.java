package com.malabe.sparedepot.persistence;

import com.malabe.sparedepot.model.Dealer;
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

public class DealerRepository {
    private final Path filePath;

    public DealerRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public List<Dealer> load() {
        List<Dealer> dealers = new ArrayList<Dealer>();
        if (!Files.exists(filePath)) {
            return dealers;
        }
        try {
            dealers = DirtyLegacyParser.parseDealers(new FileReader(filePath.toFile()));
        } catch (IOException ex) {
            return new ArrayList<Dealer>();
        }
        return dealers;
    }

    public void save(List<Dealer> dealers) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false));
        try {
            for (int i = 0; i < dealers.size(); i++) {
                writer.write(DirtyLegacyParser.toCanonicalDealerLine(dealers.get(i)));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }
}

package com.malabe.sparedepot.service;

import com.malabe.sparedepot.model.Part;
import com.malabe.sparedepot.model.SearchCriteria;
import com.malabe.sparedepot.persistence.AuditLogger;
import com.malabe.sparedepot.persistence.InventoryRepository;
import com.malabe.sparedepot.persistence.SettingsRepository;
import com.malabe.sparedepot.util.ManualSorter;
import com.malabe.sparedepot.util.ValidationUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final SettingsRepository settingsRepository;
    private final AuditLogger auditLogger;
    private final List<Part> parts;
    private int lowStockThreshold;

    public InventoryService(InventoryRepository inventoryRepository, SettingsRepository settingsRepository, AuditLogger auditLogger) {
        this.inventoryRepository = inventoryRepository;
        this.settingsRepository = settingsRepository;
        this.auditLogger = auditLogger;
        this.parts = new ArrayList<Part>();
        load();
    }

    public final void load() {
        parts.clear();
        parts.addAll(inventoryRepository.load());
        lowStockThreshold = settingsRepository.loadLowStockThreshold();
    }

    public List<Part> getAllParts() {
        List<Part> copy = new ArrayList<Part>();
        for (int i = 0; i < parts.size(); i++) {
            copy.add(parts.get(i).copy());
        }
        ManualSorter.sortPartsByCategoryThenCode(copy);
        return copy;
    }

    public List<Part> getPartsRaw() {
        return parts;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
        try {
            settingsRepository.saveLowStockThreshold(lowStockThreshold);
        } catch (IOException ex) {
            // ignore save failure for settings in UI flow
        }
    }

    public List<Part> getLowStockParts() {
        List<Part> lowStockParts = new ArrayList<Part>();
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            if (part.isLowStock(lowStockThreshold)) {
                lowStockParts.add(part.copy());
            }
        }
        ManualSorter.sortPartsByCategoryThenCode(lowStockParts);
        return lowStockParts;
    }

    public double getTotalValue() {
        double total = 0.0;
        for (int i = 0; i < parts.size(); i++) {
            total += parts.get(i).getPrice() * parts.get(i).getQuantity();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (int i = 0; i < parts.size(); i++) {
            total += parts.get(i).getQuantity();
        }
        return total;
    }

    public Part findByCode(String code) {
        if (ValidationUtil.isBlank(code)) {
            return null;
        }
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            if (part.getCode().equalsIgnoreCase(code.trim())) {
                return part;
            }
        }
        return null;
    }

    public String addPart(Part part) {
        if (part == null) {
            return "Part is required.";
        }
        if (!ValidationUtil.isValidCode(part.getCode(), 'P')) {
            return "Part code must start with P followed by digits.";
        }
        if (ValidationUtil.isBlank(part.getName())) {
            return "Part name is required.";
        }
        if (ValidationUtil.isBlank(part.getCategory())) {
            return "Part category is required.";
        }
        if (part.getPrice() <= 0.0) {
            return "Price must be positive.";
        }
        if (part.getQuantity() < 0) {
            return "Quantity cannot be negative.";
        }
        if (findByCode(part.getCode()) != null) {
            return "Part code already exists.";
        }
        parts.add(part);
        persist();
        auditLogger.log("ADD_PART", part.getCode(), String.valueOf(part.getQuantity()));
        return "Part added.";
    }

    public String updatePart(Part updatedPart) {
        if (updatedPart == null) {
            return "Part is required.";
        }
        Part existing = findByCode(updatedPart.getCode());
        if (existing == null) {
            return "Part not found.";
        }
        if (ValidationUtil.isBlank(updatedPart.getName()) || ValidationUtil.isBlank(updatedPart.getCategory())) {
            return "Name and category are required.";
        }
        if (updatedPart.getPrice() <= 0.0) {
            return "Price must be positive.";
        }
        if (updatedPart.getQuantity() < 0) {
            return "Quantity cannot be negative.";
        }
        existing.setName(updatedPart.getName());
        existing.setBrand(updatedPart.getBrand());
        existing.setPrice(updatedPart.getPrice());
        existing.setQuantity(updatedPart.getQuantity());
        existing.setCategory(updatedPart.getCategory());
        existing.setDateAdded(updatedPart.getDateAdded());
        existing.setImageFile(updatedPart.getImageFile());
        persist();
        auditLogger.log("UPDATE_PART", existing.getCode(), String.valueOf(existing.getQuantity()));
        return "Part updated.";
    }

    public String deletePart(String code) {
        Part existing = findByCode(code);
        if (existing == null) {
            return "Part not found.";
        }
        parts.remove(existing);
        persist();
        auditLogger.log("DELETE_PART", existing.getCode(), String.valueOf(existing.getQuantity()));
        return "Part deleted.";
    }

    public List<Part> search(SearchCriteria criteria) {
        List<Part> results = new ArrayList<Part>();
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            if (SearchService.matches(part, criteria)) {
                results.add(part.copy());
            }
        }
        ManualSorter.sortPartsByCategoryThenCode(results);
        return results;
    }

    public boolean deductStock(String code, int quantity) {
        Part existing = findByCode(code);
        if (existing == null || quantity <= 0 || existing.getQuantity() < quantity) {
            return false;
        }
        existing.setQuantity(existing.getQuantity() - quantity);
        persist();
        return true;
    }

    private void persist() {
        try {
            inventoryRepository.save(parts);
        } catch (IOException ex) {
            // let UI continue; persistence failure can be surfaced later if needed
        }
    }
}

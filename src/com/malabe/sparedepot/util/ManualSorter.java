package com.malabe.sparedepot.util;

import com.malabe.sparedepot.model.Dealer;
import com.malabe.sparedepot.model.Part;

import java.util.List;

public final class ManualSorter {
    private ManualSorter() {
    }

    public static void sortPartsByCategoryThenCode(List<Part> parts) {
        for (int i = 1; i < parts.size(); i++) {
            Part current = parts.get(i);
            int j = i - 1;
            while (j >= 0 && compareParts(parts.get(j), current) > 0) {
                parts.set(j + 1, parts.get(j));
                j--;
            }
            parts.set(j + 1, current);
        }
    }

    public static void sortDealersByLocation(List<Dealer> dealers) {
        for (int i = 1; i < dealers.size(); i++) {
            Dealer current = dealers.get(i);
            int j = i - 1;
            while (j >= 0 && compareDealers(dealers.get(j), current) > 0) {
                dealers.set(j + 1, dealers.get(j));
                j--;
            }
            dealers.set(j + 1, current);
        }
    }

    private static int compareParts(Part left, Part right) {
        int categoryCompare = normalize(left.getCategory()).compareToIgnoreCase(normalize(right.getCategory()));
        if (categoryCompare != 0) {
            return categoryCompare;
        }
        return normalize(left.getCode()).compareToIgnoreCase(normalize(right.getCode()));
    }

    private static int compareDealers(Dealer left, Dealer right) {
        int locationCompare = normalize(left.getLocation()).compareToIgnoreCase(normalize(right.getLocation()));
        if (locationCompare != 0) {
            return locationCompare;
        }
        return normalize(left.getCode()).compareToIgnoreCase(normalize(right.getCode()));
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}

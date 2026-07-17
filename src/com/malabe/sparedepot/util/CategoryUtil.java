package com.malabe.sparedepot.util;

public final class CategoryUtil {
    private CategoryUtil() {
    }

    public static String normalize(String category) {
        if (category == null) {
            return "";
        }
        String trimmed = category.trim();
        if (trimmed.length() == 0) {
            return "";
        }
        String upper = trimmed.toUpperCase();
        if (upper.equals("ENGINE")) {
            return "Engine";
        }
        if (upper.equals("ELECTRICAL")) {
            return "Electrical";
        }
        if (upper.equals("BRAKES")) {
            return "Brakes";
        }
        if (upper.equals("BODYWORK")) {
            return "Bodywork";
        }
        if (upper.equals("OTHER") || upper.equals("OTHERS")) {
            return "Others";
        }
        if (trimmed.length() == 1) {
            return upper;
        }
        return upper.substring(0, 1) + upper.substring(1).toLowerCase();
    }
}

package com.malabe.sparedepot.util;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isValidCode(String code, char prefix) {
        if (isBlank(code)) {
            return false;
        }
        String trimmed = code.trim().toUpperCase();
        if (trimmed.length() < 2) {
            return false;
        }
        if (trimmed.charAt(0) != prefix) {
            return false;
        }
        for (int i = 1; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPositiveNumber(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            return Double.parseDouble(value.trim()) > 0.0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            return Integer.parseInt(value.trim()) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}

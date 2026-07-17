package com.malabe.sparedepot.parser;

import com.malabe.sparedepot.model.Dealer;
import com.malabe.sparedepot.model.Part;
import com.malabe.sparedepot.util.CategoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public final class DirtyLegacyParser {
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.SMART),
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.SMART),
            DateTimeFormatter.ofPattern("MMM d uuuu").withResolverStyle(ResolverStyle.SMART),
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.SMART),
            DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.SMART),
            DateTimeFormatter.ofPattern("dd-MMM-uuuu").withResolverStyle(ResolverStyle.SMART)
    };

    private DirtyLegacyParser() {
    }

    public static List<Part> parseParts(Reader reader) throws IOException {
        List<Part> parts = new ArrayList<Part>();
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Part part = parsePartLine(line);
            if (part != null) {
                parts.add(part);
            }
        }
        return parts;
    }

    public static List<Dealer> parseDealers(Reader reader) throws IOException {
        List<Dealer> dealers = new ArrayList<Dealer>();
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Dealer dealer = parseDealerLine(line);
            if (dealer != null) {
                dealers.add(dealer);
            }
        }
        return dealers;
    }

    public static Part parsePartLine(String line) {
        List<String> tokens = tokenize(line);
        if (tokens.size() < 6) {
            return null;
        }

        String code = token(tokens, 0);
        String name = token(tokens, 1);
        String brand = token(tokens, 2);
        String priceToken = token(tokens, 3);
        String quantityToken = token(tokens, 4);
        String category = CategoryUtil.normalize(token(tokens, 5));

        Double price = parseMoney(priceToken);
        Integer quantity = parseInteger(quantityToken);
        if (code.length() == 0 || name.length() == 0 || price == null || quantity == null) {
            return null;
        }

        String dateText = "";
        String imageFile = "";
        if (tokens.size() > 6) {
            String lastToken = token(tokens, tokens.size() - 1);
            if (looksLikeImageFile(lastToken) && tokens.size() > 7) {
                imageFile = lastToken;
                dateText = join(tokens, 6, tokens.size() - 2);
            } else if (looksLikeImageFile(lastToken) && tokens.size() == 7) {
                imageFile = lastToken;
            } else {
                dateText = join(tokens, 6, tokens.size() - 1);
            }
        }

        LocalDate dateAdded = parseDate(dateText);
        return new Part(code, name, brand, price.doubleValue(), quantity.intValue(), category, dateAdded, imageFile);
    }

    public static Dealer parseDealerLine(String line) {
        List<String> tokens = tokenize(line);
        if (tokens.size() < 2) {
            return null;
        }

        String code = token(tokens, 0);
        String location = token(tokens, tokens.size() - 1);
        String phone = "";
        String name;

        if (tokens.size() >= 3 && looksLikePhone(token(tokens, tokens.size() - 2))) {
            phone = token(tokens, tokens.size() - 2);
            name = join(tokens, 1, tokens.size() - 3);
        } else {
            name = join(tokens, 1, tokens.size() - 2);
        }

        if (code.length() == 0 || name.length() == 0 || location.length() == 0) {
            return null;
        }
        return new Dealer(code, name, phone, location);
    }

    public static String toCanonicalPartLine(Part part) {
        String dateText = "";
        if (part.getDateAdded() != null) {
            dateText = part.getDateAdded().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        }
        return safe(part.getCode()) + "|" + safe(part.getName()) + "|" + safe(part.getBrand()) + "|" + trimTrailingZeros(part.getPrice()) + "|" + part.getQuantity() + "|" + safe(part.getCategory()) + "|" + dateText + "|" + safe(part.getImageFile());
    }

    public static String toCanonicalDealerLine(Dealer dealer) {
        return safe(dealer.getCode()) + "|" + safe(dealer.getName()) + "|" + safe(dealer.getPhone()) + "|" + safe(dealer.getLocation());
    }

    private static List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<String>();
        if (line == null) {
            return tokens;
        }
        String normalized = line.replace('|', ',').replace(';', ',');
        String[] raw = normalized.split(",", -1);
        for (int i = 0; i < raw.length; i++) {
            tokens.add(raw[i].trim());
        }
        return tokens;
    }

    private static String token(List<String> tokens, int index) {
        if (index < 0 || index >= tokens.size()) {
            return "";
        }
        return tokens.get(index).trim();
    }

    private static String join(List<String> tokens, int start, int end) {
        if (start > end) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = start; i <= end && i < tokens.size(); i++) {
            String current = token(tokens, i);
            if (current.length() == 0) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(current);
        }
        return builder.toString().trim();
    }

    private static boolean looksLikeImageFile(String token) {
        String lower = token.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".bmp");
    }

    private static boolean looksLikePhone(String token) {
        if (token == null) {
            return false;
        }
        String digits = token.replaceAll("\\D", "");
        return digits.length() >= 7;
    }

    private static Double parseMoney(String token) {
        if (token == null) {
            return null;
        }
        String cleaned = token.replaceAll("[^0-9.\\-]", "");
        if (cleaned.length() == 0) {
            return null;
        }
        try {
            return Double.valueOf(cleaned);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Integer parseInteger(String token) {
        if (token == null) {
            return null;
        }
        String cleaned = token.replaceAll("[^0-9\\-]", "");
        if (cleaned.length() == 0) {
            return null;
        }
        try {
            return Integer.valueOf(cleaned);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static LocalDate parseDate(String token) {
        if (token == null) {
            return null;
        }
        String trimmed = token.trim();
        if (trimmed.length() == 0) {
            return null;
        }
        for (int i = 0; i < DATE_FORMATS.length; i++) {
            try {
                return LocalDate.parse(trimmed, DATE_FORMATS[i]);
            } catch (DateTimeParseException ex) {
                // try next format
            }
        }
        return null;
    }

    private static String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private static String trimTrailingZeros(double value) {
        String text = Double.toString(value);
        if (text.indexOf('.') < 0) {
            return text;
        }
        while (text.endsWith("0")) {
            text = text.substring(0, text.length() - 1);
        }
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
}

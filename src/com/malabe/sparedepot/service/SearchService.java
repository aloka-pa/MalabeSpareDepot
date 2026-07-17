package com.malabe.sparedepot.service;

import com.malabe.sparedepot.model.Part;
import com.malabe.sparedepot.model.SearchCriteria;

public final class SearchService {
    private SearchService() {
    }

    public static boolean matches(Part part, SearchCriteria criteria) {
        if (part == null || criteria == null) {
            return false;
        }
        if (criteria.getCategory() != null && criteria.getCategory().trim().length() > 0) {
            if (!part.getCategoryKey().equals(criteria.getCategory().trim().toUpperCase())) {
                return false;
            }
        }
        if (criteria.getKeyword() != null && criteria.getKeyword().trim().length() > 0) {
            String keyword = criteria.getKeyword().trim().toLowerCase();
            String haystack = ((part.getCode() == null ? "" : part.getCode()) + " " + (part.getName() == null ? "" : part.getName()) + " " + (part.getBrand() == null ? "" : part.getBrand()) + " " + (part.getCategory() == null ? "" : part.getCategory())).toLowerCase();
            if (haystack.indexOf(keyword) < 0) {
                return false;
            }
        }
        if (criteria.getMinPrice() != null && part.getPrice() < criteria.getMinPrice().doubleValue()) {
            return false;
        }
        if (criteria.getMaxPrice() != null && part.getPrice() > criteria.getMaxPrice().doubleValue()) {
            return false;
        }
        if (criteria.getMinQuantity() != null && part.getQuantity() < criteria.getMinQuantity().intValue()) {
            return false;
        }
        return true;
    }
}

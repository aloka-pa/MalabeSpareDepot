package com.malabe.sparedepot.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<CartItem>();

    public List<CartItem> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(Part part, int quantity) {
        CartItem existing = findItem(part.getCode());
        if (existing == null) {
            items.add(new CartItem(part, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    public CartItem findItem(String code) {
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (item.getPart().getCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    public double getSubtotalBeforeDiscounts() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getLineTotalBeforeDiscount();
        }
        return total;
    }

    public double getSubtotalAfterBulkDiscounts() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getLineTotalAfterBulkDiscount();
        }
        return total;
    }

    public boolean hasEngineAndElectrical() {
        boolean engineFound = false;
        boolean electricalFound = false;
        for (int i = 0; i < items.size(); i++) {
            String categoryKey = items.get(i).getPart().getCategoryKey();
            if (categoryKey.equals("ENGINE")) {
                engineFound = true;
            }
            if (categoryKey.equals("ELECTRICAL")) {
                electricalFound = true;
            }
        }
        return engineFound && electricalFound;
    }

    public double getFinalTotal() {
        double total = getSubtotalAfterBulkDiscounts();
        if (hasEngineAndElectrical()) {
            total = total * 0.90;
        }
        return total;
    }
}

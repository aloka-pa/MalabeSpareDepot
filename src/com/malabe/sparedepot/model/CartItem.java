package com.malabe.sparedepot.model;

public class CartItem {
    private final Part part;
    private int quantity;

    public CartItem(Part part, int quantity) {
        this.part = part;
        this.quantity = quantity;
    }

    public Part getPart() {
        return part;
    }

    public String getCode() {
        return part.getCode();
    }

    public String getName() {
        return part.getName();
    }

    public String getCategory() {
        return part.getCategory();
    }

    public double getUnitPrice() {
        return part.getPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineTotalBeforeDiscount() {
        return part.getPrice() * quantity;
    }

    public double getLineTotalAfterBulkDiscount() {
        double total = getLineTotalBeforeDiscount();
        if (quantity >= 3) {
            total = total * 0.95;
        }
        return total;
    }

    public double getDisplayLineTotal() {
        return getLineTotalAfterBulkDiscount();
    }

    public boolean qualifiesForBulkDiscount() {
        return quantity >= 3;
    }
}

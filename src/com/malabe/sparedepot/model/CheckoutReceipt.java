package com.malabe.sparedepot.model;

public class CheckoutReceipt {
    private final double subtotalBeforeDiscounts;
    private final double subtotalAfterBulkDiscounts;
    private final double synergyDiscountAmount;
    private final double finalTotal;

    public CheckoutReceipt(double subtotalBeforeDiscounts, double subtotalAfterBulkDiscounts, double synergyDiscountAmount, double finalTotal) {
        this.subtotalBeforeDiscounts = subtotalBeforeDiscounts;
        this.subtotalAfterBulkDiscounts = subtotalAfterBulkDiscounts;
        this.synergyDiscountAmount = synergyDiscountAmount;
        this.finalTotal = finalTotal;
    }

    public double getSubtotalBeforeDiscounts() {
        return subtotalBeforeDiscounts;
    }

    public double getSubtotalAfterBulkDiscounts() {
        return subtotalAfterBulkDiscounts;
    }

    public double getSynergyDiscountAmount() {
        return synergyDiscountAmount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }
}

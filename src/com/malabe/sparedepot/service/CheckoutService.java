package com.malabe.sparedepot.service;

import com.malabe.sparedepot.model.Cart;
import com.malabe.sparedepot.model.CartItem;
import com.malabe.sparedepot.model.CheckoutReceipt;
import com.malabe.sparedepot.model.Part;
import com.malabe.sparedepot.persistence.AuditLogger;

public class CheckoutService {
    private final AuditLogger auditLogger;

    public CheckoutService(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    public String processCheckout(Cart cart, InventoryService inventoryService) {
        if (cart == null || cart.isEmpty()) {
            return "Cart is empty.";
        }
        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = cart.getItems().get(i);
            if (item.getQuantity() <= 0) {
                return "Invalid cart quantity.";
            }
            Part inventoryPart = inventoryService.findByCode(item.getPart().getCode());
            if (inventoryPart == null) {
                return "Part not found in inventory.";
            }
            if (inventoryPart.getQuantity() < item.getQuantity()) {
                return "Insufficient stock for " + inventoryPart.getCode();
            }
        }

        double subtotalBefore = cart.getSubtotalBeforeDiscounts();
        double subtotalAfterBulk = cart.getSubtotalAfterBulkDiscounts();
        double synergyDiscount = 0.0;
        double finalTotal = subtotalAfterBulk;
        if (cart.hasEngineAndElectrical()) {
            synergyDiscount = finalTotal * 0.10;
            finalTotal = finalTotal - synergyDiscount;
        }

        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = cart.getItems().get(i);
            inventoryService.deductStock(item.getPart().getCode(), item.getQuantity());
            auditLogger.log("CHECKOUT_ITEM", item.getPart().getCode(), String.valueOf(item.getQuantity()));
        }
        auditLogger.log("CHECKOUT", "CART", String.valueOf(cart.getItems().size()));
        cart.clear();

        CheckoutReceipt receipt = new CheckoutReceipt(subtotalBefore, subtotalAfterBulk, synergyDiscount, finalTotal);
        return "Checkout complete. Final total: " + receipt.getFinalTotal();
    }
}

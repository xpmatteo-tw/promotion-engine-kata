// ABOUTME: Represents a line in a shopping cart with product, quantity, and unit price.
// ABOUTME: Provides subtotal calculation with proper rounding.
package com.promoengine.domain;

public record LineItem(Product product, Quantity quantity, Money unitPrice) {

    public LineItem {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
    }

    /**
     * Calculates the subtotal for this line item.
     * Multiplies unit price by quantity with proper rounding.
     */
    public Money subtotal() {
        return unitPrice.multiply(quantity.intValue());
    }
}

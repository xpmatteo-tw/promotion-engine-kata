// ABOUTME: Represents a shopping cart containing line items.
// ABOUTME: Provides subtotal calculation by summing all line item subtotals.
package com.promoengine.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record Cart(List<LineItem> lines) {

    public Cart {
        if (lines == null) {
            throw new IllegalArgumentException("Lines cannot be null");
        }
        // Defensive copy to ensure immutability
        lines = List.copyOf(lines);
    }

    /**
     * Calculates the total subtotal of all line items in the cart.
     * Returns zero for an empty cart.
     */
    public Money subtotal() {
        return lines.stream()
            .map(LineItem::subtotal)
            .reduce(Money.euros(BigDecimal.ZERO), Money::add);
    }
}

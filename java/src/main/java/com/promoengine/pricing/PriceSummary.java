// ABOUTME: Summary of cart pricing including subtotal, discounts, and final total.
// ABOUTME: Provides complete transparency of all applied promotions for explainability.
package com.promoengine.pricing;

import com.promoengine.domain.Money;

import java.util.List;

public record PriceSummary(
    Money subtotal,
    Money discountTotal,
    Money total,
    List<AppliedDiscount> appliedDiscounts
) {

    public PriceSummary {
        if (subtotal == null) {
            throw new IllegalArgumentException("Subtotal cannot be null");
        }
        if (discountTotal == null) {
            throw new IllegalArgumentException("Discount total cannot be null");
        }
        if (total == null) {
            throw new IllegalArgumentException("Total cannot be null");
        }
        if (appliedDiscounts == null) {
            throw new IllegalArgumentException("Applied discounts cannot be null");
        }
        // Defensive copy to ensure immutability
        appliedDiscounts = List.copyOf(appliedDiscounts);
    }
}

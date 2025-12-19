// ABOUTME: Represents a discount that has been applied to a cart with full explainability.
// ABOUTME: Captures promotion ID, amount, target scope, details, and optional per-SKU allocations.
package com.promoengine.pricing;

import com.promoengine.domain.Money;
import com.promoengine.domain.PromotionId;
import com.promoengine.domain.Sku;

import java.util.Map;

public record AppliedDiscount(
    PromotionId promotionId,
    Money amount,
    String target,
    String details,
    Map<Sku, Money> allocations
) {

    public AppliedDiscount {
        if (promotionId == null) {
            throw new IllegalArgumentException("Promotion ID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (target == null || target.trim().isEmpty()) {
            throw new IllegalArgumentException("Target cannot be null or empty");
        }
        if (details == null) {
            throw new IllegalArgumentException("Details cannot be null");
        }
        if (allocations == null) {
            allocations = Map.of();
        } else {
            // Defensive copy to ensure immutability
            allocations = Map.copyOf(allocations);
        }
    }

    // Convenience constructor without allocations
    public AppliedDiscount(PromotionId promotionId, Money amount, String target, String details) {
        this(promotionId, amount, target, details, Map.of());
    }

    // TODO: Consider adding "reason" field for why promotion was not applied
    // TODO: Consider adding "evaluationTrace" for detailed decision logging
}

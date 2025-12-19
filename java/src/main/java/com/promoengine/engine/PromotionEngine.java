// ABOUTME: Orchestrates promotion evaluation and pricing calculation for shopping carts.
// ABOUTME: Applies applicable promotions and generates comprehensive pricing summaries.
package com.promoengine.engine;

import com.promoengine.domain.Cart;
import com.promoengine.domain.Money;
import com.promoengine.pricing.AppliedDiscount;
import com.promoengine.pricing.PriceSummary;
import com.promoengine.pricing.PricingContext;
import com.promoengine.promotions.Promotion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PromotionEngine {

    private final List<Promotion> promotions;

    public PromotionEngine(List<Promotion> promotions) {
        if (promotions == null) {
            throw new IllegalArgumentException("Promotions cannot be null");
        }
        this.promotions = List.copyOf(promotions);
    }

    /**
     * Calculates pricing for a cart with applicable promotions.
     *
     * Algorithm:
     * 1. Calculate cart subtotal
     * 2. Filter promotions by applicability
     * 3. Collect discounts from applicable promotions
     * 4. Sum all discounts
     * 5. Calculate final total (subtotal - discountTotal)
     * 6. Return comprehensive summary
     *
     * @param cart The shopping cart to price
     * @param context The pricing context (time, channel, customer)
     * @return Complete pricing summary with explainability
     */
    public PriceSummary price(Cart cart, PricingContext context) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        // 1. Calculate cart subtotal
        Money subtotal = cart.subtotal();

        // 2-3. Find applicable promotions and collect their discounts
        List<AppliedDiscount> allDiscounts = new ArrayList<>();
        for (Promotion promotion : promotions) {
            if (promotion.isApplicable(cart, context)) {
                List<AppliedDiscount> discounts = promotion.apply(cart, context);
                allDiscounts.addAll(discounts);
            }
        }

        // 4. Sum all discount amounts
        Money discountTotal = allDiscounts.stream()
            .map(AppliedDiscount::amount)
            .reduce(Money.euros(BigDecimal.ZERO), Money::add);

        // 5. Calculate final total
        Money total = subtotal.subtract(discountTotal);

        // 6. Return summary
        return new PriceSummary(subtotal, discountTotal, total, allDiscounts);
    }

    // TODO: Add promotion priority/ordering support
    // TODO: Add promotion combination rules (STACK, EXCLUSIVE_BEST, EXCLUSIVE_PRIORITY)
    // TODO: Consider tracking which promotions were evaluated but not applicable
    // TODO: Consider promotion budget tracking
}

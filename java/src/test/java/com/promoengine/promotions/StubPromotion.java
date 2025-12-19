// ABOUTME: Test helper that implements Promotion with configurable behavior.
// ABOUTME: Allows testing of promotion engine without implementing real promotion logic.
package com.promoengine.promotions;

import com.promoengine.domain.Cart;
import com.promoengine.domain.PromotionId;
import com.promoengine.pricing.AppliedDiscount;
import com.promoengine.pricing.PricingContext;

import java.util.List;

public class StubPromotion implements Promotion {

    private final PromotionId promotionId;
    private final boolean shouldApply;
    private final List<AppliedDiscount> discounts;

    public StubPromotion(PromotionId promotionId, boolean shouldApply, List<AppliedDiscount> discounts) {
        this.promotionId = promotionId;
        this.shouldApply = shouldApply;
        this.discounts = discounts != null ? List.copyOf(discounts) : List.of();
    }

    @Override
    public PromotionId id() {
        return promotionId;
    }

    @Override
    public boolean isApplicable(Cart cart, PricingContext context) {
        return shouldApply;
    }

    @Override
    public List<AppliedDiscount> apply(Cart cart, PricingContext context) {
        return discounts;
    }
}

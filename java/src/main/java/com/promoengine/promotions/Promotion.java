// ABOUTME: Interface defining the contract for promotion implementations.
// ABOUTME: Promotions evaluate cart eligibility and generate discount applications.
package com.promoengine.promotions;

import com.promoengine.domain.Cart;
import com.promoengine.domain.PromotionId;
import com.promoengine.pricing.AppliedDiscount;
import com.promoengine.pricing.PricingContext;

import java.util.List;

public interface Promotion {
    /**
     * Returns the unique identifier for this promotion.
     */
    PromotionId id();

    /**
     * Determines if this promotion is applicable to the given cart and context.
     * @param cart The shopping cart to evaluate
     * @param context The pricing context (time, channel, customer)
     * @return true if the promotion should be applied
     */
    boolean isApplicable(Cart cart, PricingContext context);

    /**
     * Applies this promotion to the cart and returns the resulting discounts.
     * Should only be called if isApplicable returns true.
     * @param cart The shopping cart
     * @param context The pricing context
     * @return List of applied discounts with explainability
     */
    List<AppliedDiscount> apply(Cart cart, PricingContext context);

    // TODO: Add time-based applicability (validFrom, validUntil)
    // TODO: Add customer segment targeting using context.customerTags
    // TODO: Consider PercentOffProductPromotion implementation
    // TODO: Consider BuyXGetYPromotion for quantity-based discounts
    // TODO: Consider ThresholdPromotion (spend $X, save $Y)
}

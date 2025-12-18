// ABOUTME: PromotionEngine applies promotions to carts and calculates final prices.
// ABOUTME: Orchestrates promotion evaluation and discount application.
package engine

import (
	"promotionengine/domain"
	"promotionengine/pricing"
	"promotionengine/promotions"
)

// PromotionEngine is responsible for pricing carts with promotions.
type PromotionEngine struct {
	promotionsList []promotions.Promotion
}

// NewPromotionEngine creates a new PromotionEngine with the given promotions.
func NewPromotionEngine(promos []promotions.Promotion) *PromotionEngine {
	return &PromotionEngine{
		promotionsList: promos,
	}
}

// Price calculates the final price for a cart, applying all applicable promotions.
func (e *PromotionEngine) Price(cart domain.Cart, context pricing.PricingContext) pricing.PriceSummary {
	subtotal := cart.Subtotal()

	// Collect all applicable discounts
	var appliedDiscounts []pricing.AppliedDiscount
	for _, promo := range e.promotionsList {
		if promo.IsApplicable(cart, context) {
			discounts := promo.Apply(cart, context)
			appliedDiscounts = append(appliedDiscounts, discounts...)
		}
	}

	// Calculate total discount
	discountTotal := domain.CentsAmount(0)
	for _, discount := range appliedDiscounts {
		discountTotal = discountTotal.Add(discount.Amount)
	}

	// Calculate final total
	total := subtotal.Subtract(discountTotal)

	return pricing.PriceSummary{
		Subtotal:         subtotal,
		DiscountTotal:    discountTotal,
		Total:            total,
		AppliedDiscounts: appliedDiscounts,
	}
}

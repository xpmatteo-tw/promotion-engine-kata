// ABOUTME: PromotionEngine applies promotions to carts and calculates final prices.
// ABOUTME: Orchestrates promotion evaluation and discount application.
package promotionengine

// PromotionEngine is responsible for pricing carts with promotions.
type PromotionEngine struct {
	promotions []Promotion
}

// NewPromotionEngine creates a new PromotionEngine with the given promotions.
func NewPromotionEngine(promotions []Promotion) *PromotionEngine {
	return &PromotionEngine{
		promotions: promotions,
	}
}

// Price calculates the final price for a cart, applying all applicable promotions.
func (e *PromotionEngine) Price(cart Cart, context PricingContext) PriceSummary {
	subtotal := cart.Subtotal()

	// Collect all applicable discounts
	var appliedDiscounts []AppliedDiscount
	for _, promo := range e.promotions {
		if promo.IsApplicable(cart, context) {
			discounts := promo.Apply(cart, context)
			appliedDiscounts = append(appliedDiscounts, discounts...)
		}
	}

	// Calculate total discount
	discountTotal := CentsAmount(0)
	for _, discount := range appliedDiscounts {
		discountTotal = discountTotal.Add(discount.Amount)
	}

	// Calculate final total
	total := subtotal.Subtract(discountTotal)

	return PriceSummary{
		Subtotal:         subtotal,
		DiscountTotal:    discountTotal,
		Total:            total,
		AppliedDiscounts: appliedDiscounts,
	}
}

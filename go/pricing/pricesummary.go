// ABOUTME: PriceSummary domain type representing the final pricing result.
// ABOUTME: Contains subtotal, discount total, final total, and applied discounts.
package pricing

import "promotionengine/domain"

// PriceSummary represents the result of pricing a cart.
type PriceSummary struct {
	Subtotal         domain.Money
	DiscountTotal    domain.Money
	Total            domain.Money
	AppliedDiscounts []AppliedDiscount
}

// NewPriceSummary creates a new PriceSummary.
func NewPriceSummary(subtotal, discountTotal, total domain.Money, appliedDiscounts []AppliedDiscount) PriceSummary {
	return PriceSummary{
		Subtotal:         subtotal,
		DiscountTotal:    discountTotal,
		Total:            total,
		AppliedDiscounts: appliedDiscounts,
	}
}

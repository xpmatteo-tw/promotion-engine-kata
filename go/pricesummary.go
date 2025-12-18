// ABOUTME: PriceSummary domain type representing the final pricing result.
// ABOUTME: Contains subtotal, discount total, final total, and applied discounts.
package promotionengine

// PriceSummary represents the result of pricing a cart.
type PriceSummary struct {
	Subtotal         Money
	DiscountTotal    Money
	Total            Money
	AppliedDiscounts []AppliedDiscount
}

// NewPriceSummary creates a new PriceSummary.
func NewPriceSummary(subtotal, discountTotal, total Money, appliedDiscounts []AppliedDiscount) PriceSummary {
	return PriceSummary{
		Subtotal:         subtotal,
		DiscountTotal:    discountTotal,
		Total:            total,
		AppliedDiscounts: appliedDiscounts,
	}
}

// ABOUTME: AppliedDiscount domain type representing a discount applied to a cart or line item.
// ABOUTME: Preserves explainability with promotion ID, amount, target, details, and optional allocations.
package promotionengine

// AppliedDiscount represents a discount that has been applied.
// It preserves explainability by tracking the promotion, amount, target, and details.
type AppliedDiscount struct {
	PromotionID PromotionID
	Amount      Money
	Target      string // "line" or "cart"
	Details     string
	Allocations map[Sku]Money // Optional: per-SKU discount allocations
}

// NewAppliedDiscount creates a new AppliedDiscount without allocations.
func NewAppliedDiscount(promotionID PromotionID, amount Money, target, details string) AppliedDiscount {
	return AppliedDiscount{
		PromotionID: promotionID,
		Amount:      amount,
		Target:      target,
		Details:     details,
		Allocations: nil,
	}
}

// NewAppliedDiscountWithAllocations creates a new AppliedDiscount with per-SKU allocations.
func NewAppliedDiscountWithAllocations(promotionID PromotionID, amount Money, target, details string, allocations map[Sku]Money) AppliedDiscount {
	return AppliedDiscount{
		PromotionID: promotionID,
		Amount:      amount,
		Target:      target,
		Details:     details,
		Allocations: allocations,
	}
}

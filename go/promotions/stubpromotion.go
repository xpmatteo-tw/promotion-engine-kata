// ABOUTME: StubPromotion is a test stub for the Promotion interface.
// ABOUTME: Allows configurable behavior for testing the promotion engine.
package promotions

import (
	"promotionengine/domain"
	"promotionengine/pricing"
)

// StubPromotion is a test stub implementation of the Promotion interface.
// It allows you to configure its behavior for testing purposes.
type StubPromotion struct {
	PromoID         domain.PromotionID
	DiscountAmount  domain.Money
	ShouldApply     bool
	Target          string // "line" or "cart"
	DiscountDetails string
}

// ID returns the promotion identifier.
func (s *StubPromotion) ID() domain.PromotionID {
	return s.PromoID
}

// IsApplicable returns whether this promotion should apply.
func (s *StubPromotion) IsApplicable(cart domain.Cart, context pricing.PricingContext) bool {
	return s.ShouldApply
}

// Apply returns a configured discount if applicable.
func (s *StubPromotion) Apply(cart domain.Cart, context pricing.PricingContext) []pricing.AppliedDiscount {
	if !s.ShouldApply {
		return []pricing.AppliedDiscount{}
	}

	discount := pricing.NewAppliedDiscount(s.PromoID, s.DiscountAmount, s.Target, s.DiscountDetails)
	return []pricing.AppliedDiscount{discount}
}

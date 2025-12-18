// ABOUTME: StubPromotion is a test stub for the Promotion interface.
// ABOUTME: Allows configurable behavior for testing the promotion engine.
package promotionengine

// StubPromotion is a test stub implementation of the Promotion interface.
// It allows you to configure its behavior for testing purposes.
type StubPromotion struct {
	PromoID         PromotionID
	DiscountAmount  Money
	ShouldApply     bool
	Target          string // "line" or "cart"
	DiscountDetails string
}

// ID returns the promotion identifier.
func (s *StubPromotion) ID() PromotionID {
	return s.PromoID
}

// IsApplicable returns whether this promotion should apply.
func (s *StubPromotion) IsApplicable(cart Cart, context PricingContext) bool {
	return s.ShouldApply
}

// Apply returns a configured discount if applicable.
func (s *StubPromotion) Apply(cart Cart, context PricingContext) []AppliedDiscount {
	if !s.ShouldApply {
		return []AppliedDiscount{}
	}

	discount := NewAppliedDiscount(s.PromoID, s.DiscountAmount, s.Target, s.DiscountDetails)
	return []AppliedDiscount{discount}
}

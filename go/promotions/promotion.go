// ABOUTME: Promotion interface defining the contract for promotions.
// ABOUTME: Promotions can check applicability and apply discounts to carts.
package promotions

import (
	"promotionengine/domain"
	"promotionengine/pricing"
)

// Promotion represents a promotional discount that can be applied to a cart.
// TODO: Implement specific promotion types (e.g., PercentOffdomain.Cart, BuyXGetY, etc.)
type Promotion interface {
	// ID returns the unique identifier for this promotion.
	ID() domain.PromotionID

	// IsApplicable checks if this promotion can be applied to the given cart and context.
	IsApplicable(cart domain.Cart, context pricing.PricingContext) bool

	// Apply applies this promotion to the cart and returns the resulting discounts.
	Apply(cart domain.Cart, context pricing.PricingContext) []pricing.AppliedDiscount
}

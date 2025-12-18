// ABOUTME: Acceptance tests for the promotion engine system.
// ABOUTME: Tests end-to-end behavior of pricing carts with promotions.
package engine_test

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"promotionengine/domain"
	"promotionengine/engine"
	"promotionengine/pricing"
	"promotionengine/promotions"
)

// TestPromotionEngineWithNoPromotions verifies that the engine calculates
// correct prices for a cart with no promotions applied.
func TestPromotionEngineWithNoPromotions(t *testing.T) {
	// Create products
	apple := domain.Product{
		Sku:      domain.Sku{Value: "APPLE"},
		Name:     "Apple",
		Category: "Fruit",
	}
	banana := domain.Product{
		Sku:      domain.Sku{Value: "BANANA"},
		Name:     "Banana",
		Category: "Fruit",
	}

	// Create cart with line items
	cart := domain.Cart{
		Lines: []domain.LineItem{
			{
				Product:   apple,
				Quantity:  domain.Quantity{Value: 3},
				UnitPrice: domain.Euros(1.50),
			},
			{
				Product:   banana,
				Quantity:  domain.Quantity{Value: 2},
				UnitPrice: domain.Euros(0.75),
			},
		},
	}

	// Create pricing context
	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := pricing.PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	// Create engine with no promotions
	eng := engine.NewPromotionEngine([]promotions.Promotion{})

	// Price the cart
	summary := eng.Price(cart, context)

	// Verify results
	expectedSubtotal := domain.Euros(1.50).Multiply(3).Add(domain.Euros(0.75).Multiply(2))
	assert.Equal(t, expectedSubtotal, summary.Subtotal, "Subtotal should be sum of line totals")
	assert.Equal(t, domain.CentsAmount(0), summary.DiscountTotal, "No discounts should be applied")
	assert.Equal(t, expectedSubtotal, summary.Total, "Total should equal subtotal with no discounts")
	assert.Empty(t, summary.AppliedDiscounts, "No discounts should be in the list")
}

// TestPromotionEngineWithStubPromotion verifies that the engine correctly
// applies a simple stub promotion to demonstrate the promotion mechanism.
func TestPromotionEngineWithStubPromotion(t *testing.T) {
	// Create products
	apple := domain.Product{
		Sku:      domain.Sku{Value: "APPLE"},
		Name:     "Apple",
		Category: "Fruit",
	}

	// Create cart
	cart := domain.Cart{
		Lines: []domain.LineItem{
			{
				Product:   apple,
				Quantity:  domain.Quantity{Value: 3},
				UnitPrice: domain.Euros(1.50),
			},
		},
	}

	// Create pricing context
	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := pricing.PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	// Create a stub promotion that gives 10% off
	stubPromo := &promotions.StubPromotion{
		PromoID:          domain.PromotionID{Value: "STUB10"},
		DiscountAmount:   domain.Euros(0.45), // 10% of 4.50
		ShouldApply:      true,
		Target:           "cart",
		DiscountDetails:  "Stub 10% discount",
	}

	// Create engine with stub promotion
	eng := engine.NewPromotionEngine([]promotions.Promotion{stubPromo})

	// Price the cart
	summary := eng.Price(cart, context)

	// Verify results
	expectedSubtotal := domain.Euros(4.50)
	expectedDiscount := domain.Euros(0.45)
	expectedTotal := domain.Euros(4.05)

	assert.Equal(t, expectedSubtotal, summary.Subtotal, "Subtotal should be 4.50")
	assert.Equal(t, expectedDiscount, summary.DiscountTotal, "Discount should be 0.45")
	assert.Equal(t, expectedTotal, summary.Total, "Total should be 4.05")
	require.Len(t, summary.AppliedDiscounts, 1, "One discount should be applied")

	discount := summary.AppliedDiscounts[0]
	assert.Equal(t, domain.PromotionID{Value: "STUB10"}, discount.PromotionID)
	assert.Equal(t, expectedDiscount, discount.Amount)
	assert.Equal(t, "cart", discount.Target)
	assert.Equal(t, "Stub 10% discount", discount.Details)
}

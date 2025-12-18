// ABOUTME: Unit tests for the PromotionEngine.
// ABOUTME: Verifies promotion application and price calculation.
package promotionengine

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestPromotionEngineNoPromotions(t *testing.T) {
	apple := Product{
		Sku:      MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}

	cart := Cart{
		Lines: []LineItem{
			{
				Product:   apple,
				Quantity:  MustQuantity(3),
				UnitPrice: Euros(1.50),
			},
		},
	}

	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	engine := NewPromotionEngine([]Promotion{})
	summary := engine.Price(cart, context)

	assert.Equal(t, Euros(4.50), summary.Subtotal)
	assert.Equal(t, CentsAmount(0), summary.DiscountTotal)
	assert.Equal(t, Euros(4.50), summary.Total)
	assert.Empty(t, summary.AppliedDiscounts)
}

func TestPromotionEngineWithApplicablePromotion(t *testing.T) {
	apple := Product{
		Sku:      MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}

	cart := Cart{
		Lines: []LineItem{
			{
				Product:   apple,
				Quantity:  MustQuantity(3),
				UnitPrice: Euros(1.50),
			},
		},
	}

	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	stubPromo := &StubPromotion{
		PromoID:         MustPromotionID("STUB10"),
		DiscountAmount:  Euros(0.45),
		ShouldApply:     true,
		Target:          "cart",
		DiscountDetails: "Stub 10% discount",
	}

	engine := NewPromotionEngine([]Promotion{stubPromo})
	summary := engine.Price(cart, context)

	assert.Equal(t, Euros(4.50), summary.Subtotal)
	assert.Equal(t, Euros(0.45), summary.DiscountTotal)
	assert.Equal(t, Euros(4.05), summary.Total)
	assert.Len(t, summary.AppliedDiscounts, 1)
	assert.Equal(t, MustPromotionID("STUB10"), summary.AppliedDiscounts[0].PromotionID)
}

func TestPromotionEngineWithNonApplicablePromotion(t *testing.T) {
	apple := Product{
		Sku:      MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}

	cart := Cart{
		Lines: []LineItem{
			{
				Product:   apple,
				Quantity:  MustQuantity(3),
				UnitPrice: Euros(1.50),
			},
		},
	}

	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	stubPromo := &StubPromotion{
		PromoID:         MustPromotionID("STUB10"),
		DiscountAmount:  Euros(0.45),
		ShouldApply:     false, // Not applicable
		Target:          "cart",
		DiscountDetails: "Stub 10% discount",
	}

	engine := NewPromotionEngine([]Promotion{stubPromo})
	summary := engine.Price(cart, context)

	assert.Equal(t, Euros(4.50), summary.Subtotal)
	assert.Equal(t, CentsAmount(0), summary.DiscountTotal)
	assert.Equal(t, Euros(4.50), summary.Total)
	assert.Empty(t, summary.AppliedDiscounts)
}

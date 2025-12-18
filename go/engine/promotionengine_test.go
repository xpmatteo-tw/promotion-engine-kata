// ABOUTME: Unit tests for the PromotionEngine.
// ABOUTME: Verifies promotion application and price calculation.
package engine

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"promotionengine/domain"
	"promotionengine/pricing"
	"promotionengine/promotions"
)

func TestPromotionEngineNoPromotions(t *testing.T) {
	apple := domain.Product{
		Sku:      domain.MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}

	cart := domain.Cart{
		Lines: []domain.LineItem{
			{
				Product:   apple,
				Quantity:  domain.MustQuantity(3),
				UnitPrice: domain.Euros(1.50),
			},
		},
	}

	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := pricing.PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	engine := NewPromotionEngine([]promotions.Promotion{})
	summary := engine.Price(cart, context)

	assert.Equal(t, domain.Euros(4.50), summary.Subtotal)
	assert.Equal(t, domain.CentsAmount(0), summary.DiscountTotal)
	assert.Equal(t, domain.Euros(4.50), summary.Total)
	assert.Empty(t, summary.AppliedDiscounts)
}

func TestPromotionEngineWithApplicablePromotion(t *testing.T) {
	apple := domain.Product{
		Sku:      domain.MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}

	cart := domain.Cart{
		Lines: []domain.LineItem{
			{
				Product:   apple,
				Quantity:  domain.MustQuantity(3),
				UnitPrice: domain.Euros(1.50),
			},
		},
	}

	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := pricing.PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	stubPromo := &promotions.StubPromotion{
		PromoID:         domain.MustPromotionID("STUB10"),
		DiscountAmount:  domain.Euros(0.45),
		ShouldApply:     true,
		Target:          "cart",
		DiscountDetails: "Stub 10% discount",
	}

	engine := NewPromotionEngine([]promotions.Promotion{stubPromo})
	summary := engine.Price(cart, context)

	assert.Equal(t, domain.Euros(4.50), summary.Subtotal)
	assert.Equal(t, domain.Euros(0.45), summary.DiscountTotal)
	assert.Equal(t, domain.Euros(4.05), summary.Total)
	assert.Len(t, summary.AppliedDiscounts, 1)
	assert.Equal(t, domain.MustPromotionID("STUB10"), summary.AppliedDiscounts[0].PromotionID)
}

func TestPromotionEngineWithNonApplicablePromotion(t *testing.T) {
	apple := domain.Product{
		Sku:      domain.MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}

	cart := domain.Cart{
		Lines: []domain.LineItem{
			{
				Product:   apple,
				Quantity:  domain.MustQuantity(3),
				UnitPrice: domain.Euros(1.50),
			},
		},
	}

	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := pricing.PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true},
	}

	stubPromo := &promotions.StubPromotion{
		PromoID:         domain.MustPromotionID("STUB10"),
		DiscountAmount:  domain.Euros(0.45),
		ShouldApply:     false, // Not applicable
		Target:          "cart",
		DiscountDetails: "Stub 10% discount",
	}

	engine := NewPromotionEngine([]promotions.Promotion{stubPromo})
	summary := engine.Price(cart, context)

	assert.Equal(t, domain.Euros(4.50), summary.Subtotal)
	assert.Equal(t, domain.CentsAmount(0), summary.DiscountTotal)
	assert.Equal(t, domain.Euros(4.50), summary.Total)
	assert.Empty(t, summary.AppliedDiscounts)
}

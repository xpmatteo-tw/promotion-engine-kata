// ABOUTME: Unit tests for the AppliedDiscount domain type.
// ABOUTME: Verifies discount creation with and without allocations.
package pricing

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"promotionengine/domain"
)

func TestNewAppliedDiscount(t *testing.T) {
	promoID := domain.MustPromotionID("PROMO10")
	amount := domain.Euros(5.00)
	target := "cart"
	details := "10% off cart"

	discount := NewAppliedDiscount(promoID, amount, target, details)

	assert.Equal(t, promoID, discount.PromotionID)
	assert.Equal(t, amount, discount.Amount)
	assert.Equal(t, target, discount.Target)
	assert.Equal(t, details, discount.Details)
	assert.Nil(t, discount.Allocations)
}

func TestNewAppliedDiscountWithAllocations(t *testing.T) {
	promoID := domain.MustPromotionID("PROMO10")
	amount := domain.Euros(5.00)
	target := "line"
	details := "10% off per line"

	appleSku := domain.MustSku("APPLE")
	bananaSku := domain.MustSku("BANANA")
	allocations := map[domain.Sku]domain.Money{
		appleSku:  domain.Euros(3.00),
		bananaSku: domain.Euros(2.00),
	}

	discount := NewAppliedDiscountWithAllocations(promoID, amount, target, details, allocations)

	assert.Equal(t, promoID, discount.PromotionID)
	assert.Equal(t, amount, discount.Amount)
	assert.Equal(t, target, discount.Target)
	assert.Equal(t, details, discount.Details)
	assert.NotNil(t, discount.Allocations)
	assert.Equal(t, domain.Euros(3.00), discount.Allocations[appleSku])
	assert.Equal(t, domain.Euros(2.00), discount.Allocations[bananaSku])
}

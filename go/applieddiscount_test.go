// ABOUTME: Unit tests for the AppliedDiscount domain type.
// ABOUTME: Verifies discount creation with and without allocations.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewAppliedDiscount(t *testing.T) {
	promoID := MustPromotionID("PROMO10")
	amount := Euros(5.00)
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
	promoID := MustPromotionID("PROMO10")
	amount := Euros(5.00)
	target := "line"
	details := "10% off per line"

	appleSku := MustSku("APPLE")
	bananaSku := MustSku("BANANA")
	allocations := map[Sku]Money{
		appleSku:  Euros(3.00),
		bananaSku: Euros(2.00),
	}

	discount := NewAppliedDiscountWithAllocations(promoID, amount, target, details, allocations)

	assert.Equal(t, promoID, discount.PromotionID)
	assert.Equal(t, amount, discount.Amount)
	assert.Equal(t, target, discount.Target)
	assert.Equal(t, details, discount.Details)
	assert.NotNil(t, discount.Allocations)
	assert.Equal(t, Euros(3.00), discount.Allocations[appleSku])
	assert.Equal(t, Euros(2.00), discount.Allocations[bananaSku])
}

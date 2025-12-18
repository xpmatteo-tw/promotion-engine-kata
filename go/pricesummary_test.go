// ABOUTME: Unit tests for the PriceSummary domain type.
// ABOUTME: Verifies price summary creation and field assignment.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewPriceSummary(t *testing.T) {
	subtotal := Euros(10.00)
	discountTotal := Euros(2.00)
	total := Euros(8.00)

	discount := NewAppliedDiscount(
		MustPromotionID("PROMO10"),
		Euros(2.00),
		"cart",
		"10% off",
	)
	appliedDiscounts := []AppliedDiscount{discount}

	summary := NewPriceSummary(subtotal, discountTotal, total, appliedDiscounts)

	assert.Equal(t, subtotal, summary.Subtotal)
	assert.Equal(t, discountTotal, summary.DiscountTotal)
	assert.Equal(t, total, summary.Total)
	assert.Len(t, summary.AppliedDiscounts, 1)
	assert.Equal(t, discount, summary.AppliedDiscounts[0])
}

func TestNewPriceSummaryNoDiscounts(t *testing.T) {
	subtotal := Euros(10.00)
	discountTotal := Euros(0.00)
	total := Euros(10.00)
	appliedDiscounts := []AppliedDiscount{}

	summary := NewPriceSummary(subtotal, discountTotal, total, appliedDiscounts)

	assert.Equal(t, subtotal, summary.Subtotal)
	assert.Equal(t, discountTotal, summary.DiscountTotal)
	assert.Equal(t, total, summary.Total)
	assert.Empty(t, summary.AppliedDiscounts)
}

// ABOUTME: Unit tests for the PriceSummary domain type.
// ABOUTME: Verifies price summary creation and field assignment.
package pricing

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"promotionengine/domain"
)

func TestNewPriceSummary(t *testing.T) {
	subtotal := domain.Euros(10.00)
	discountTotal := domain.Euros(2.00)
	total := domain.Euros(8.00)

	discount := NewAppliedDiscount(
		domain.MustPromotionID("PROMO10"),
		domain.Euros(2.00),
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
	subtotal := domain.Euros(10.00)
	discountTotal := domain.Euros(0.00)
	total := domain.Euros(10.00)
	appliedDiscounts := []AppliedDiscount{}

	summary := NewPriceSummary(subtotal, discountTotal, total, appliedDiscounts)

	assert.Equal(t, subtotal, summary.Subtotal)
	assert.Equal(t, discountTotal, summary.DiscountTotal)
	assert.Equal(t, total, summary.Total)
	assert.Empty(t, summary.AppliedDiscounts)
}

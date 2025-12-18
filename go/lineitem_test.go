// ABOUTME: Unit tests for the LineItem domain type.
// ABOUTME: Verifies line item creation and total calculation.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewLineItem(t *testing.T) {
	product := Product{
		Sku:      MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}
	quantity := MustQuantity(3)
	unitPrice := Euros(1.50)

	lineItem := NewLineItem(product, quantity, unitPrice)

	assert.Equal(t, product, lineItem.Product)
	assert.Equal(t, quantity, lineItem.Quantity)
	assert.Equal(t, unitPrice, lineItem.UnitPrice)
}

func TestLineItemTotal(t *testing.T) {
	tests := []struct {
		name      string
		quantity  int
		unitPrice Money
		expected  Money
	}{
		{"single item", 1, Euros(1.50), Euros(1.50)},
		{"multiple items", 3, Euros(1.50), Euros(4.50)},
		{"zero quantity", 0, Euros(1.50), Euros(0.00)},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			product := Product{
				Sku:      MustSku("APPLE"),
				Name:     "Apple",
				Category: "Fruit",
			}
			lineItem := LineItem{
				Product:   product,
				Quantity:  MustQuantity(tt.quantity),
				UnitPrice: tt.unitPrice,
			}

			total := lineItem.Total()
			assert.Equal(t, tt.expected, total)
		})
	}
}

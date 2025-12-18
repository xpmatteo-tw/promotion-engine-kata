// ABOUTME: Unit tests for the Cart domain type.
// ABOUTME: Verifies cart creation, line item management, and subtotal calculation.
package domain

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewCart(t *testing.T) {
	cart := NewCart()
	assert.Empty(t, cart.Lines)
}

func TestCartAddLine(t *testing.T) {
	cart := NewCart()

	product := Product{
		Sku:      MustSku("APPLE"),
		Name:     "Apple",
		Category: "Fruit",
	}
	line := LineItem{
		Product:   product,
		Quantity:  MustQuantity(3),
		UnitPrice: Euros(1.50),
	}

	cart.AddLine(line)

	assert.Len(t, cart.Lines, 1)
	assert.Equal(t, line, cart.Lines[0])
}

func TestCartSubtotal(t *testing.T) {
	tests := []struct {
		name     string
		lines    []LineItem
		expected Money
	}{
		{
			name:     "empty cart",
			lines:    []LineItem{},
			expected: Euros(0.00),
		},
		{
			name: "single line",
			lines: []LineItem{
				{
					Product: Product{
						Sku:      MustSku("APPLE"),
						Name:     "Apple",
						Category: "Fruit",
					},
					Quantity:  MustQuantity(3),
					UnitPrice: Euros(1.50),
				},
			},
			expected: Euros(4.50),
		},
		{
			name: "multiple lines",
			lines: []LineItem{
				{
					Product: Product{
						Sku:      MustSku("APPLE"),
						Name:     "Apple",
						Category: "Fruit",
					},
					Quantity:  MustQuantity(3),
					UnitPrice: Euros(1.50),
				},
				{
					Product: Product{
						Sku:      MustSku("BANANA"),
						Name:     "Banana",
						Category: "Fruit",
					},
					Quantity:  MustQuantity(2),
					UnitPrice: Euros(0.75),
				},
			},
			expected: Euros(6.00),
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			cart := Cart{Lines: tt.lines}
			subtotal := cart.Subtotal()
			assert.Equal(t, tt.expected, subtotal)
		})
	}
}

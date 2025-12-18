// ABOUTME: LineItem domain type representing a product line in a cart.
// ABOUTME: Contains product, quantity, and pricing information.
package domain

// LineItem represents a line item in a shopping cart.
type LineItem struct {
	Product   Product
	Quantity  Quantity
	UnitPrice Money
}

// NewLineItem creates a new LineItem.
func NewLineItem(product Product, quantity Quantity, unitPrice Money) LineItem {
	return LineItem{
		Product:   product,
		Quantity:  quantity,
		UnitPrice: unitPrice,
	}
}

// Total calculates the total price for this line item.
func (l LineItem) Total() Money {
	return l.UnitPrice.Multiply(l.Quantity.Value)
}

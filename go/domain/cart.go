// ABOUTME: Cart domain type representing a shopping cart with line items.
// ABOUTME: Provides methods to calculate subtotals and manage items.
package domain

// Cart represents a shopping cart containing line items.
type Cart struct {
	Lines []LineItem
}

// NewCart creates a new empty Cart.
func NewCart() Cart {
	return Cart{Lines: []LineItem{}}
}

// AddLine adds a line item to the cart.
func (c *Cart) AddLine(line LineItem) {
	c.Lines = append(c.Lines, line)
}

// Subtotal calculates the total price of all items in the cart.
func (c Cart) Subtotal() Money {
	total := CentsAmount(0)
	for _, line := range c.Lines {
		total = total.Add(line.Total())
	}
	return total
}

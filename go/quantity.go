// ABOUTME: Quantity value type representing a non-negative integer quantity.
// ABOUTME: Provides validation to ensure quantities are not negative.
package promotionengine

import (
	"fmt"
)

// Quantity represents a non-negative quantity value.
type Quantity struct {
	Value int
}

// NewQuantity creates a new Quantity value.
// Returns an error if the value is negative.
func NewQuantity(value int) (Quantity, error) {
	if value < 0 {
		return Quantity{}, fmt.Errorf("quantity must be >= 0, got %d", value)
	}
	return Quantity{Value: value}, nil
}

// MustQuantity creates a new Quantity value and panics if invalid.
// Use this when you're certain the value is valid.
func MustQuantity(value int) Quantity {
	q, err := NewQuantity(value)
	if err != nil {
		panic(err)
	}
	return q
}

// Add returns a new Quantity that is the sum of this and other.
func (q Quantity) Add(other Quantity) Quantity {
	return Quantity{Value: q.Value + other.Value}
}

// Subtract returns a new Quantity that is the difference of this and other.
// Returns an error if the result would be negative.
func (q Quantity) Subtract(other Quantity) (Quantity, error) {
	result := q.Value - other.Value
	if result < 0 {
		return Quantity{}, fmt.Errorf("quantity subtraction would result in negative value: %d - %d", q.Value, other.Value)
	}
	return Quantity{Value: result}, nil
}

// String returns a string representation of the quantity.
func (q Quantity) String() string {
	return fmt.Sprintf("%d", q.Value)
}

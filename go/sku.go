// ABOUTME: Sku value type representing a stock keeping unit identifier.
// ABOUTME: Wraps a string to provide type safety for product identifiers.
package promotionengine

import (
	"fmt"
)

// Sku represents a stock keeping unit identifier.
type Sku struct {
	Value string
}

// NewSku creates a new Sku value.
// Returns an error if the value is empty.
func NewSku(value string) (Sku, error) {
	if value == "" {
		return Sku{}, fmt.Errorf("sku cannot be empty")
	}
	return Sku{Value: value}, nil
}

// MustSku creates a new Sku value and panics if invalid.
// Use this when you're certain the value is valid.
func MustSku(value string) Sku {
	s, err := NewSku(value)
	if err != nil {
		panic(err)
	}
	return s
}

// String returns the string representation of the Sku.
func (s Sku) String() string {
	return s.Value
}

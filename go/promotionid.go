// ABOUTME: PromotionID value type representing a promotion identifier.
// ABOUTME: Wraps a string to provide type safety for promotion identifiers.
package promotionengine

import (
	"fmt"
)

// PromotionID represents a promotion identifier.
type PromotionID struct {
	Value string
}

// NewPromotionID creates a new PromotionID value.
// Returns an error if the value is empty.
func NewPromotionID(value string) (PromotionID, error) {
	if value == "" {
		return PromotionID{}, fmt.Errorf("promotion id cannot be empty")
	}
	return PromotionID{Value: value}, nil
}

// MustPromotionID creates a new PromotionID value and panics if invalid.
// Use this when you're certain the value is valid.
func MustPromotionID(value string) PromotionID {
	p, err := NewPromotionID(value)
	if err != nil {
		panic(err)
	}
	return p
}

// String returns the string representation of the PromotionID.
func (p PromotionID) String() string {
	return p.Value
}

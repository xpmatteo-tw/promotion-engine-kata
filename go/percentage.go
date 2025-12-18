// ABOUTME: Percentage value type representing a percentage between 0 and 100.
// ABOUTME: Provides validation to ensure percentages are within valid range.
package promotionengine

import (
	"fmt"
)

// Percentage represents a percentage value between 0 and 100.
type Percentage struct {
	Value float64
}

// NewPercentage creates a new Percentage value.
// Returns an error if the value is not between 0 and 100.
func NewPercentage(value float64) (Percentage, error) {
	if value < 0 || value > 100 {
		return Percentage{}, fmt.Errorf("percentage must be between 0 and 100, got %.2f", value)
	}
	return Percentage{Value: value}, nil
}

// MustPercentage creates a new Percentage value and panics if invalid.
// Use this when you're certain the value is valid.
func MustPercentage(value float64) Percentage {
	p, err := NewPercentage(value)
	if err != nil {
		panic(err)
	}
	return p
}

// AsDecimal returns the percentage as a decimal (e.g., 10% -> 0.1).
func (p Percentage) AsDecimal() float64 {
	return p.Value / 100.0
}

// String returns a string representation of the percentage.
func (p Percentage) String() string {
	return fmt.Sprintf("%.2f%%", p.Value)
}

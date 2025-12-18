// ABOUTME: Money value type representing monetary amounts in cents.
// ABOUTME: Provides safe arithmetic operations and proper rounding behavior.
package domain

import (
	"fmt"
	"math"
)

// Money represents a monetary amount stored as cents (1/100 of the base unit).
// This avoids floating-point precision issues in financial calculations.
type Money struct {
	Cents int64
}

// Euros creates a Money value from a euro amount.
// The value is quantized to 2 decimal places using ROUND_HALF_UP.
func Euros(amount float64) Money {
	cents := roundHalfUp(amount * 100)
	return Money{Cents: cents}
}

// CentsAmount creates a Money value from a cent amount.
func CentsAmount(cents int64) Money {
	return Money{Cents: cents}
}

// roundHalfUp rounds a float64 to the nearest integer using ROUND_HALF_UP.
// Values exactly halfway between two integers are rounded up.
// A small epsilon is added to handle floating-point precision issues.
func roundHalfUp(value float64) int64 {
	const epsilon = 1e-9
	if value >= 0 {
		return int64(math.Floor(value + 0.5 + epsilon))
	}
	return int64(math.Ceil(value - 0.5 - epsilon))
}

// Add returns a new Money value that is the sum of this and other.
func (m Money) Add(other Money) Money {
	return Money{Cents: m.Cents + other.Cents}
}

// Subtract returns a new Money value that is the difference of this and other.
func (m Money) Subtract(other Money) Money {
	return Money{Cents: m.Cents - other.Cents}
}

// Multiply returns a new Money value that is this amount multiplied by factor.
// The result is quantized to cents using ROUND_HALF_UP.
func (m Money) Multiply(factor int) Money {
	return Money{Cents: m.Cents * int64(factor)}
}

// MultiplyFloat returns a new Money value that is this amount multiplied by factor.
// The result is quantized to cents using ROUND_HALF_UP.
func (m Money) MultiplyFloat(factor float64) Money {
	result := float64(m.Cents) * factor
	return Money{Cents: roundHalfUp(result)}
}

// Equal returns true if this Money value equals other.
func (m Money) Equal(other Money) bool {
	return m.Cents == other.Cents
}

// LessThan returns true if this Money value is less than other.
func (m Money) LessThan(other Money) bool {
	return m.Cents < other.Cents
}

// LessThanOrEqual returns true if this Money value is less than or equal to other.
func (m Money) LessThanOrEqual(other Money) bool {
	return m.Cents <= other.Cents
}

// GreaterThan returns true if this Money value is greater than other.
func (m Money) GreaterThan(other Money) bool {
	return m.Cents > other.Cents
}

// GreaterThanOrEqual returns true if this Money value is greater than or equal to other.
func (m Money) GreaterThanOrEqual(other Money) bool {
	return m.Cents >= other.Cents
}

// String returns a string representation of the Money value in euros.
func (m Money) String() string {
	euros := float64(m.Cents) / 100.0
	return fmt.Sprintf("€%.2f", euros)
}

// ABOUTME: Unit tests for the Money value type.
// ABOUTME: Verifies arithmetic operations, rounding, and comparisons.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestEuros(t *testing.T) {
	tests := []struct {
		name     string
		euros    float64
		expected int64
	}{
		{"whole euros", 5.0, 500},
		{"with cents", 1.50, 150},
		{"round half up", 1.555, 156},
		{"round half up from .5", 1.005, 101},
		{"negative amount", -1.50, -150},
		{"zero", 0.0, 0},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			money := Euros(tt.euros)
			assert.Equal(t, tt.expected, money.Cents)
		})
	}
}

func TestCentsAmount(t *testing.T) {
	money := CentsAmount(150)
	assert.Equal(t, int64(150), money.Cents)
}

func TestMoneyAdd(t *testing.T) {
	m1 := Euros(1.50)
	m2 := Euros(2.25)
	result := m1.Add(m2)
	assert.Equal(t, Euros(3.75), result)
}

func TestMoneySubtract(t *testing.T) {
	m1 := Euros(5.00)
	m2 := Euros(2.25)
	result := m1.Subtract(m2)
	assert.Equal(t, Euros(2.75), result)
}

func TestMoneyMultiply(t *testing.T) {
	m := Euros(1.50)
	result := m.Multiply(3)
	assert.Equal(t, Euros(4.50), result)
}

func TestMoneyMultiplyFloat(t *testing.T) {
	tests := []struct {
		name     string
		money    Money
		factor   float64
		expected Money
	}{
		{"multiply by 0.5", Euros(10.00), 0.5, Euros(5.00)},
		{"multiply by 0.1 with rounding", Euros(4.50), 0.1, Euros(0.45)},
		{"multiply by percentage", Euros(10.00), 0.15, Euros(1.50)},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := tt.money.MultiplyFloat(tt.factor)
			assert.Equal(t, tt.expected, result)
		})
	}
}

func TestMoneyComparisons(t *testing.T) {
	m1 := Euros(1.50)
	m2 := Euros(2.50)
	m3 := Euros(1.50)

	assert.True(t, m1.Equal(m3))
	assert.False(t, m1.Equal(m2))

	assert.True(t, m1.LessThan(m2))
	assert.False(t, m2.LessThan(m1))

	assert.True(t, m1.LessThanOrEqual(m2))
	assert.True(t, m1.LessThanOrEqual(m3))
	assert.False(t, m2.LessThanOrEqual(m1))

	assert.True(t, m2.GreaterThan(m1))
	assert.False(t, m1.GreaterThan(m2))

	assert.True(t, m2.GreaterThanOrEqual(m1))
	assert.True(t, m1.GreaterThanOrEqual(m3))
	assert.False(t, m1.GreaterThanOrEqual(m2))
}

func TestMoneyString(t *testing.T) {
	tests := []struct {
		name     string
		money    Money
		expected string
	}{
		{"whole euros", Euros(5.00), "€5.00"},
		{"with cents", Euros(1.50), "€1.50"},
		{"zero", CentsAmount(0), "€0.00"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equal(t, tt.expected, tt.money.String())
		})
	}
}

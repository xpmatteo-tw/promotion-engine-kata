// ABOUTME: Unit tests for the Quantity value type.
// ABOUTME: Verifies validation and arithmetic operations.
package domain

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestNewQuantity(t *testing.T) {
	tests := []struct {
		name      string
		value     int
		expectErr bool
	}{
		{"valid zero", 0, false},
		{"valid positive", 10, false},
		{"invalid negative", -1, true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			q, err := NewQuantity(tt.value)
			if tt.expectErr {
				assert.Error(t, err)
			} else {
				require.NoError(t, err)
				assert.Equal(t, tt.value, q.Value)
			}
		})
	}
}

func TestMustQuantity(t *testing.T) {
	t.Run("valid value", func(t *testing.T) {
		q := MustQuantity(10)
		assert.Equal(t, 10, q.Value)
	})

	t.Run("invalid value panics", func(t *testing.T) {
		assert.Panics(t, func() {
			MustQuantity(-1)
		})
	})
}

func TestQuantityAdd(t *testing.T) {
	q1 := MustQuantity(5)
	q2 := MustQuantity(3)
	result := q1.Add(q2)
	assert.Equal(t, 8, result.Value)
}

func TestQuantitySubtract(t *testing.T) {
	tests := []struct {
		name      string
		q1        Quantity
		q2        Quantity
		expected  int
		expectErr bool
	}{
		{"valid subtraction", MustQuantity(5), MustQuantity(3), 2, false},
		{"subtract to zero", MustQuantity(5), MustQuantity(5), 0, false},
		{"invalid subtraction", MustQuantity(3), MustQuantity(5), 0, true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result, err := tt.q1.Subtract(tt.q2)
			if tt.expectErr {
				assert.Error(t, err)
			} else {
				require.NoError(t, err)
				assert.Equal(t, tt.expected, result.Value)
			}
		})
	}
}

func TestQuantityString(t *testing.T) {
	q := MustQuantity(42)
	assert.Equal(t, "42", q.String())
}

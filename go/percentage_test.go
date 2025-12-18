// ABOUTME: Unit tests for the Percentage value type.
// ABOUTME: Verifies validation and conversion to decimal.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestNewPercentage(t *testing.T) {
	tests := []struct {
		name      string
		value     float64
		expectErr bool
	}{
		{"valid zero", 0, false},
		{"valid middle", 50, false},
		{"valid max", 100, false},
		{"invalid negative", -1, true},
		{"invalid over 100", 101, true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p, err := NewPercentage(tt.value)
			if tt.expectErr {
				assert.Error(t, err)
			} else {
				require.NoError(t, err)
				assert.Equal(t, tt.value, p.Value)
			}
		})
	}
}

func TestMustPercentage(t *testing.T) {
	t.Run("valid value", func(t *testing.T) {
		p := MustPercentage(50)
		assert.Equal(t, 50.0, p.Value)
	})

	t.Run("invalid value panics", func(t *testing.T) {
		assert.Panics(t, func() {
			MustPercentage(150)
		})
	})
}

func TestPercentageAsDecimal(t *testing.T) {
	tests := []struct {
		name     string
		percent  float64
		expected float64
	}{
		{"zero", 0, 0.0},
		{"ten percent", 10, 0.1},
		{"fifty percent", 50, 0.5},
		{"one hundred percent", 100, 1.0},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := MustPercentage(tt.percent)
			assert.Equal(t, tt.expected, p.AsDecimal())
		})
	}
}

func TestPercentageString(t *testing.T) {
	tests := []struct {
		name     string
		percent  float64
		expected string
	}{
		{"zero", 0, "0.00%"},
		{"ten percent", 10, "10.00%"},
		{"fifty percent", 50.5, "50.50%"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := MustPercentage(tt.percent)
			assert.Equal(t, tt.expected, p.String())
		})
	}
}

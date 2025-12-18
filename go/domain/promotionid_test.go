// ABOUTME: Unit tests for the PromotionID value type.
// ABOUTME: Verifies validation and string representation.
package domain

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestNewPromotionID(t *testing.T) {
	tests := []struct {
		name      string
		value     string
		expectErr bool
	}{
		{"valid promo id", "PROMO123", false},
		{"valid with special chars", "WINTER-SALE-2025", false},
		{"invalid empty", "", true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p, err := NewPromotionID(tt.value)
			if tt.expectErr {
				assert.Error(t, err)
			} else {
				require.NoError(t, err)
				assert.Equal(t, tt.value, p.Value)
			}
		})
	}
}

func TestMustPromotionID(t *testing.T) {
	t.Run("valid value", func(t *testing.T) {
		p := MustPromotionID("PROMO123")
		assert.Equal(t, "PROMO123", p.Value)
	})

	t.Run("invalid value panics", func(t *testing.T) {
		assert.Panics(t, func() {
			MustPromotionID("")
		})
	})
}

func TestPromotionIDString(t *testing.T) {
	p := MustPromotionID("PROMO123")
	assert.Equal(t, "PROMO123", p.String())
}

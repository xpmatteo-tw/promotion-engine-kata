// ABOUTME: Unit tests for the Sku value type.
// ABOUTME: Verifies validation and string representation.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestNewSku(t *testing.T) {
	tests := []struct {
		name      string
		value     string
		expectErr bool
	}{
		{"valid sku", "APPLE", false},
		{"valid with numbers", "PROD123", false},
		{"invalid empty", "", true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			s, err := NewSku(tt.value)
			if tt.expectErr {
				assert.Error(t, err)
			} else {
				require.NoError(t, err)
				assert.Equal(t, tt.value, s.Value)
			}
		})
	}
}

func TestMustSku(t *testing.T) {
	t.Run("valid value", func(t *testing.T) {
		s := MustSku("APPLE")
		assert.Equal(t, "APPLE", s.Value)
	})

	t.Run("invalid value panics", func(t *testing.T) {
		assert.Panics(t, func() {
			MustSku("")
		})
	})
}

func TestSkuString(t *testing.T) {
	s := MustSku("APPLE")
	assert.Equal(t, "APPLE", s.String())
}

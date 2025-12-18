// ABOUTME: Unit tests for the PricingContext type.
// ABOUTME: Verifies context creation and tag checking.
package pricing

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestNewPricingContext(t *testing.T) {
	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	channel := "online"
	customerID := "CUST123"
	customerTags := map[string]bool{"new": true, "gold": true}

	context := NewPricingContext(now, channel, customerID, customerTags)

	assert.Equal(t, now, context.Now)
	assert.Equal(t, channel, context.Channel)
	assert.Equal(t, customerID, context.CustomerID)
	assert.Equal(t, customerTags, context.CustomerTags)
}

func TestPricingContextHasTag(t *testing.T) {
	now := time.Date(2025, 12, 18, 10, 0, 0, 0, time.UTC)
	context := PricingContext{
		Now:          now,
		Channel:      "online",
		CustomerID:   "CUST123",
		CustomerTags: map[string]bool{"new": true, "gold": true},
	}

	assert.True(t, context.HasTag("new"))
	assert.True(t, context.HasTag("gold"))
	assert.False(t, context.HasTag("vip"))
}

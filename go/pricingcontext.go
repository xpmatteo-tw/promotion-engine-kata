// ABOUTME: PricingContext type containing contextual information for pricing.
// ABOUTME: Includes timestamp, channel, customer ID, and customer tags for promotion eligibility.
package promotionengine

import (
	"time"
)

// PricingContext contains contextual information for pricing a cart.
type PricingContext struct {
	Now          time.Time
	Channel      string
	CustomerID   string
	CustomerTags map[string]bool // Set of customer tags (e.g., {"new": true, "gold": true})
}

// NewPricingContext creates a new PricingContext.
func NewPricingContext(now time.Time, channel, customerID string, customerTags map[string]bool) PricingContext {
	return PricingContext{
		Now:          now,
		Channel:      channel,
		CustomerID:   customerID,
		CustomerTags: customerTags,
	}
}

// HasTag checks if a customer has a specific tag.
func (c PricingContext) HasTag(tag string) bool {
	return c.CustomerTags[tag]
}

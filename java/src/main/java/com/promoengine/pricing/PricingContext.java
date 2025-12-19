// ABOUTME: Context information for promotion evaluation including time, channel, and customer data.
// ABOUTME: Immutable context passed to promotions for applicability and discount calculation.
package com.promoengine.pricing;

import java.time.Instant;
import java.util.Set;

public record PricingContext(
    Instant now,
    String channel,
    String customerId,
    Set<String> customerTags
) {

    public PricingContext {
        if (now == null) {
            throw new IllegalArgumentException("Now cannot be null");
        }
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel cannot be null or empty");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (customerTags == null) {
            throw new IllegalArgumentException("Customer tags cannot be null");
        }
        // Defensive copy to ensure immutability
        customerTags = Set.copyOf(customerTags);
    }

    public boolean hasTag(String tag) {
        return customerTags.contains(tag);
    }
}

// ABOUTME: Type-safe wrapper for promotion identifiers.
// ABOUTME: Ensures promotion IDs are non-empty strings.
package com.promoengine.domain;

public record PromotionId(String value) {

    public PromotionId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PromotionId cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

// ABOUTME: Type-safe wrapper for stock keeping unit identifiers.
// ABOUTME: Ensures SKUs are non-empty strings and provides hashable identity.
package com.promoengine.domain;

public record Sku(String value) {

    public Sku {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

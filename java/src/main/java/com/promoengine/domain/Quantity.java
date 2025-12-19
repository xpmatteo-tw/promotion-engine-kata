// ABOUTME: Type-safe wrapper for non-negative quantity values.
// ABOUTME: Ensures quantities are always zero or positive integers.
package com.promoengine.domain;

public record Quantity(int value) {

    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative, got: " + value);
        }
    }

    public int intValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

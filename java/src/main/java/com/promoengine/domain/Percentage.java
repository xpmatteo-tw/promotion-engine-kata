// ABOUTME: Type-safe wrapper for percentage values in the range 0-100.
// ABOUTME: Provides conversion to decimal multiplier for calculations.
package com.promoengine.domain;

import java.math.BigDecimal;

public record Percentage(BigDecimal value) {

    public Percentage {
        if (value == null) {
            throw new IllegalArgumentException("Percentage cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, got: " + value);
        }
    }

    public static Percentage of(String value) {
        return new Percentage(new BigDecimal(value));
    }

    public static Percentage of(double value) {
        return new Percentage(BigDecimal.valueOf(value));
    }

    public BigDecimal asDecimal() {
        return value.divide(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return value + "%";
    }
}

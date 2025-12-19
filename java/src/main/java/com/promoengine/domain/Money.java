// ABOUTME: Immutable monetary value using BigDecimal for precision.
// ABOUTME: Provides arithmetic operations with ROUND_HALF_UP rounding.
package com.promoengine.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) implements Comparable<Money> {

    // Compact constructor for validation and quantization
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        // Quantize to 2 decimal places using ROUND_HALF_UP
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    // Factory methods for convenient construction

    public static Money euros(String amount) {
        return new Money(new BigDecimal(amount));
    }

    public static Money euros(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money euros(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money cents(long cents) {
        return new Money(BigDecimal.valueOf(cents).divide(
            BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    // Arithmetic operations

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null Money");
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null Money");
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Cannot multiply by null factor");
        return new Money(this.amount.multiply(factor));
    }

    // Comparison

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Cannot compare to null Money");
        return this.amount.compareTo(other.amount);
    }

    public boolean isLessThan(Money other) {
        return this.compareTo(other) < 0;
    }

    public boolean isGreaterThan(Money other) {
        return this.compareTo(other) > 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public String toString() {
        return "€" + amount;
    }
}

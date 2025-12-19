// ABOUTME: Unit tests for Quantity value type.
// ABOUTME: Verifies non-negative validation and value access.
package com.promoengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QuantityTest {

    @Test
    void shouldConstructWithValidQuantity() {
        Quantity qty = new Quantity(5);
        assertThat(qty.value()).isEqualTo(5);
    }

    @Test
    void shouldAcceptZero() {
        Quantity qty = new Quantity(0);
        assertThat(qty.value()).isEqualTo(0);
    }

    @Test
    void shouldRejectNegative() {
        assertThatThrownBy(() -> new Quantity(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be negative");
    }

    @Test
    void shouldConvertToInt() {
        Quantity qty = new Quantity(5);
        assertThat(qty.intValue()).isEqualTo(5);
    }

    @Test
    void shouldFormatAsString() {
        Quantity qty = new Quantity(5);
        assertThat(qty.toString()).isEqualTo("5");
    }

    @Test
    void shouldBeEqualWithSameValue() {
        Quantity qty1 = new Quantity(5);
        Quantity qty2 = new Quantity(5);
        assertThat(qty1).isEqualTo(qty2);
    }
}

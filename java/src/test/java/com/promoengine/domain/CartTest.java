// ABOUTME: Unit tests for Cart domain model.
// ABOUTME: Verifies construction and subtotal calculation from line items.
package com.promoengine.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CartTest {

    @Test
    void shouldConstructWithValidLines() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        LineItem line = new LineItem(product, new Quantity(1), Money.euros("10.00"));
        Cart cart = new Cart(List.of(line));

        assertThat(cart.lines()).hasSize(1);
        assertThat(cart.lines().get(0)).isEqualTo(line);
    }

    @Test
    void shouldCalculateSubtotalForEmptyCart() {
        Cart cart = new Cart(List.of());
        assertThat(cart.subtotal()).isEqualTo(Money.euros("0.00"));
    }

    @Test
    void shouldCalculateSubtotalForSingleLine() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        LineItem line = new LineItem(product, new Quantity(2), Money.euros("10.00"));
        Cart cart = new Cart(List.of(line));

        assertThat(cart.subtotal()).isEqualTo(Money.euros("20.00"));
    }

    @Test
    void shouldCalculateSubtotalForMultipleLines() {
        Product productA = new Product(new Sku("SKU-A"), "Product A", "electronics");
        Product productB = new Product(new Sku("SKU-B"), "Product B", "books");

        LineItem line1 = new LineItem(productA, new Quantity(2), Money.euros("10.00"));
        LineItem line2 = new LineItem(productB, new Quantity(1), Money.euros("25.00"));

        Cart cart = new Cart(List.of(line1, line2));

        // 2*10 + 1*25 = 45
        assertThat(cart.subtotal()).isEqualTo(Money.euros("45.00"));
    }

    @Test
    void shouldCalculateSubtotalWithRounding() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        LineItem line1 = new LineItem(product, new Quantity(3), Money.euros("10.33"));
        LineItem line2 = new LineItem(product, new Quantity(2), Money.euros("5.11"));

        Cart cart = new Cart(List.of(line1, line2));

        // 3*10.33 + 2*5.11 = 30.99 + 10.22 = 41.21
        assertThat(cart.subtotal()).isEqualTo(Money.euros("41.21"));
    }

    @Test
    void shouldRejectNullLines() {
        assertThatThrownBy(() -> new Cart(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Lines cannot be null");
    }

    @Test
    void shouldBeImmutable() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        LineItem line = new LineItem(product, new Quantity(1), Money.euros("10.00"));
        Cart cart = new Cart(List.of(line));

        // Attempting to modify the returned list should not affect the cart
        assertThatThrownBy(() -> cart.lines().add(line))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}

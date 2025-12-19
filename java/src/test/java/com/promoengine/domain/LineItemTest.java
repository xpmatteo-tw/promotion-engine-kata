// ABOUTME: Unit tests for LineItem domain model.
// ABOUTME: Verifies construction and subtotal calculation with rounding.
package com.promoengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LineItemTest {

    @Test
    void shouldConstructWithValidFields() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        Quantity quantity = new Quantity(2);
        Money unitPrice = Money.euros("10.00");

        LineItem lineItem = new LineItem(product, quantity, unitPrice);

        assertThat(lineItem.product()).isEqualTo(product);
        assertThat(lineItem.quantity()).isEqualTo(quantity);
        assertThat(lineItem.unitPrice()).isEqualTo(unitPrice);
    }

    @Test
    void shouldCalculateSubtotal() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        Quantity quantity = new Quantity(3);
        Money unitPrice = Money.euros("10.00");

        LineItem lineItem = new LineItem(product, quantity, unitPrice);

        assertThat(lineItem.subtotal()).isEqualTo(Money.euros("30.00"));
    }

    @Test
    void shouldCalculateSubtotalWithRounding() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        Quantity quantity = new Quantity(3);
        Money unitPrice = Money.euros("10.33");

        LineItem lineItem = new LineItem(product, quantity, unitPrice);

        // 3 * 10.33 = 30.99
        assertThat(lineItem.subtotal()).isEqualTo(Money.euros("30.99"));
    }

    @Test
    void shouldCalculateSubtotalForZeroQuantity() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        Quantity quantity = new Quantity(0);
        Money unitPrice = Money.euros("10.00");

        LineItem lineItem = new LineItem(product, quantity, unitPrice);

        assertThat(lineItem.subtotal()).isEqualTo(Money.euros("0.00"));
    }

    @Test
    void shouldRejectNullProduct() {
        assertThatThrownBy(() -> new LineItem(null, new Quantity(1), Money.euros("10.00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product cannot be null");
    }

    @Test
    void shouldRejectNullQuantity() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        assertThatThrownBy(() -> new LineItem(product, null, Money.euros("10.00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantity cannot be null");
    }

    @Test
    void shouldRejectNullUnitPrice() {
        Product product = new Product(new Sku("SKU-123"), "Product Name", "electronics");
        assertThatThrownBy(() -> new LineItem(product, new Quantity(1), null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unit price cannot be null");
    }
}

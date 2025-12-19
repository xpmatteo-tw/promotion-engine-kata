// ABOUTME: Unit tests for Product domain model.
// ABOUTME: Verifies construction and immutability.
package com.promoengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    void shouldConstructWithValidFields() {
        Sku sku = new Sku("SKU-123");
        Product product = new Product(sku, "Product Name", "electronics");

        assertThat(product.sku()).isEqualTo(sku);
        assertThat(product.name()).isEqualTo("Product Name");
        assertThat(product.category()).isEqualTo("electronics");
    }

    @Test
    void shouldRejectNullSku() {
        assertThatThrownBy(() -> new Product(null, "Product Name", "electronics"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SKU cannot be null");
    }

    @Test
    void shouldRejectNullName() {
        Sku sku = new Sku("SKU-123");
        assertThatThrownBy(() -> new Product(sku, null, "electronics"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("name cannot be null or empty");
    }

    @Test
    void shouldRejectEmptyName() {
        Sku sku = new Sku("SKU-123");
        assertThatThrownBy(() -> new Product(sku, "", "electronics"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("name cannot be null or empty");
    }

    @Test
    void shouldRejectNullCategory() {
        Sku sku = new Sku("SKU-123");
        assertThatThrownBy(() -> new Product(sku, "Product Name", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("category cannot be null or empty");
    }

    @Test
    void shouldRejectEmptyCategory() {
        Sku sku = new Sku("SKU-123");
        assertThatThrownBy(() -> new Product(sku, "Product Name", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("category cannot be null or empty");
    }

    @Test
    void shouldBeEqualWithSameValues() {
        Sku sku = new Sku("SKU-123");
        Product p1 = new Product(sku, "Product Name", "electronics");
        Product p2 = new Product(sku, "Product Name", "electronics");
        assertThat(p1).isEqualTo(p2);
    }
}

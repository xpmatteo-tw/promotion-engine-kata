// ABOUTME: Represents a product with identity, name, and category.
// ABOUTME: Immutable value object combining product attributes.
package com.promoengine.domain;

public record Product(Sku sku, String name, String category) {

    public Product {
        if (sku == null) {
            throw new IllegalArgumentException("SKU cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
    }
}

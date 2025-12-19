// ABOUTME: Unit tests for Sku value type.
// ABOUTME: Verifies non-empty validation and hashability.
package com.promoengine.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class SkuTest {

    @Test
    void shouldConstructWithValidSku() {
        Sku sku = new Sku("SKU-123");
        assertThat(sku.value()).isEqualTo("SKU-123");
    }

    @Test
    void shouldRejectNull() {
        assertThatThrownBy(() -> new Sku(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void shouldRejectEmpty() {
        assertThatThrownBy(() -> new Sku(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void shouldRejectWhitespace() {
        assertThatThrownBy(() -> new Sku("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void shouldFormatAsString() {
        Sku sku = new Sku("SKU-123");
        assertThat(sku.toString()).isEqualTo("SKU-123");
    }

    @Test
    void shouldBeHashable() {
        Sku sku1 = new Sku("SKU-123");
        Sku sku2 = new Sku("SKU-123");
        Set<Sku> set = new HashSet<>();
        set.add(sku1);
        assertThat(set).contains(sku2);
    }

    @Test
    void shouldBeEqualWithSameValue() {
        Sku sku1 = new Sku("SKU-123");
        Sku sku2 = new Sku("SKU-123");
        assertThat(sku1).isEqualTo(sku2);
    }

    @Test
    void shouldNotBeEqualWithDifferentValue() {
        Sku sku1 = new Sku("SKU-123");
        Sku sku2 = new Sku("SKU-456");
        assertThat(sku1).isNotEqualTo(sku2);
    }
}

// ABOUTME: Unit tests for PromotionId value type.
// ABOUTME: Verifies non-empty validation and identity.
package com.promoengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PromotionIdTest {

    @Test
    void shouldConstructWithValidId() {
        PromotionId id = new PromotionId("PROMO-123");
        assertThat(id.value()).isEqualTo("PROMO-123");
    }

    @Test
    void shouldRejectNull() {
        assertThatThrownBy(() -> new PromotionId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void shouldRejectEmpty() {
        assertThatThrownBy(() -> new PromotionId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void shouldRejectWhitespace() {
        assertThatThrownBy(() -> new PromotionId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void shouldFormatAsString() {
        PromotionId id = new PromotionId("PROMO-123");
        assertThat(id.toString()).isEqualTo("PROMO-123");
    }

    @Test
    void shouldBeEqualWithSameValue() {
        PromotionId id1 = new PromotionId("PROMO-123");
        PromotionId id2 = new PromotionId("PROMO-123");
        assertThat(id1).isEqualTo(id2);
    }

    @Test
    void shouldNotBeEqualWithDifferentValue() {
        PromotionId id1 = new PromotionId("PROMO-123");
        PromotionId id2 = new PromotionId("PROMO-456");
        assertThat(id1).isNotEqualTo(id2);
    }
}

// ABOUTME: Unit tests for AppliedDiscount.
// ABOUTME: Verifies construction with and without allocations.
package com.promoengine.pricing;

import com.promoengine.domain.Money;
import com.promoengine.domain.PromotionId;
import com.promoengine.domain.Sku;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class AppliedDiscountTest {

    @Test
    void shouldConstructWithAllocations() {
        PromotionId promotionId = new PromotionId("PROMO-123");
        Money amount = Money.euros("10.00");
        Map<Sku, Money> allocations = Map.of(
            new Sku("SKU-A"), Money.euros("5.00"),
            new Sku("SKU-B"), Money.euros("5.00")
        );

        AppliedDiscount discount = new AppliedDiscount(
            promotionId,
            amount,
            "cart",
            "Test discount",
            allocations
        );

        assertThat(discount.promotionId()).isEqualTo(promotionId);
        assertThat(discount.amount()).isEqualTo(amount);
        assertThat(discount.target()).isEqualTo("cart");
        assertThat(discount.details()).isEqualTo("Test discount");
        assertThat(discount.allocations()).hasSize(2);
        assertThat(discount.allocations().get(new Sku("SKU-A"))).isEqualTo(Money.euros("5.00"));
    }

    @Test
    void shouldConstructWithoutAllocations() {
        PromotionId promotionId = new PromotionId("PROMO-123");
        Money amount = Money.euros("10.00");

        AppliedDiscount discount = new AppliedDiscount(
            promotionId,
            amount,
            "line",
            "Test discount"
        );

        assertThat(discount.promotionId()).isEqualTo(promotionId);
        assertThat(discount.amount()).isEqualTo(amount);
        assertThat(discount.target()).isEqualTo("line");
        assertThat(discount.details()).isEqualTo("Test discount");
        assertThat(discount.allocations()).isEmpty();
    }

    @Test
    void shouldHandleNullAllocationsAsEmpty() {
        PromotionId promotionId = new PromotionId("PROMO-123");
        Money amount = Money.euros("10.00");

        AppliedDiscount discount = new AppliedDiscount(
            promotionId,
            amount,
            "line",
            "Test discount",
            null
        );

        assertThat(discount.allocations()).isEmpty();
    }

    @Test
    void shouldRejectNullPromotionId() {
        assertThatThrownBy(() -> new AppliedDiscount(
            null,
            Money.euros("10.00"),
            "line",
            "Test discount"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Promotion ID cannot be null");
    }

    @Test
    void shouldRejectNullAmount() {
        assertThatThrownBy(() -> new AppliedDiscount(
            new PromotionId("PROMO-123"),
            null,
            "line",
            "Test discount"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Amount cannot be null");
    }

    @Test
    void shouldRejectNullTarget() {
        assertThatThrownBy(() -> new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("10.00"),
            null,
            "Test discount"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Target cannot be null or empty");
    }

    @Test
    void shouldRejectNullDetails() {
        assertThatThrownBy(() -> new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("10.00"),
            "line",
            null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Details cannot be null");
    }

    @Test
    void shouldBeImmutable() {
        Map<Sku, Money> allocations = Map.of(new Sku("SKU-A"), Money.euros("5.00"));
        AppliedDiscount discount = new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("10.00"),
            "line",
            "Test discount",
            allocations
        );

        assertThatThrownBy(() -> discount.allocations().put(new Sku("SKU-B"), Money.euros("5.00")))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}

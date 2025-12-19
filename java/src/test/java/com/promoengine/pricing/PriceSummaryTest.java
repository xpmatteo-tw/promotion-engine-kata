// ABOUTME: Unit tests for PriceSummary.
// ABOUTME: Verifies construction and field access.
package com.promoengine.pricing;

import com.promoengine.domain.Money;
import com.promoengine.domain.PromotionId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PriceSummaryTest {

    @Test
    void shouldConstructWithValidFields() {
        Money subtotal = Money.euros("100.00");
        Money discountTotal = Money.euros("10.00");
        Money total = Money.euros("90.00");
        AppliedDiscount discount = new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("10.00"),
            "cart",
            "Test discount"
        );
        List<AppliedDiscount> appliedDiscounts = List.of(discount);

        PriceSummary summary = new PriceSummary(subtotal, discountTotal, total, appliedDiscounts);

        assertThat(summary.subtotal()).isEqualTo(subtotal);
        assertThat(summary.discountTotal()).isEqualTo(discountTotal);
        assertThat(summary.total()).isEqualTo(total);
        assertThat(summary.appliedDiscounts()).hasSize(1);
        assertThat(summary.appliedDiscounts().get(0)).isEqualTo(discount);
    }

    @Test
    void shouldHandleEmptyDiscounts() {
        PriceSummary summary = new PriceSummary(
            Money.euros("100.00"),
            Money.euros("0.00"),
            Money.euros("100.00"),
            List.of()
        );

        assertThat(summary.appliedDiscounts()).isEmpty();
    }

    @Test
    void shouldRejectNullSubtotal() {
        assertThatThrownBy(() -> new PriceSummary(
            null,
            Money.euros("10.00"),
            Money.euros("90.00"),
            List.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Subtotal cannot be null");
    }

    @Test
    void shouldRejectNullDiscountTotal() {
        assertThatThrownBy(() -> new PriceSummary(
            Money.euros("100.00"),
            null,
            Money.euros("90.00"),
            List.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Discount total cannot be null");
    }

    @Test
    void shouldRejectNullTotal() {
        assertThatThrownBy(() -> new PriceSummary(
            Money.euros("100.00"),
            Money.euros("10.00"),
            null,
            List.of()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Total cannot be null");
    }

    @Test
    void shouldRejectNullAppliedDiscounts() {
        assertThatThrownBy(() -> new PriceSummary(
            Money.euros("100.00"),
            Money.euros("10.00"),
            Money.euros("90.00"),
            null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Applied discounts cannot be null");
    }

    @Test
    void shouldBeImmutable() {
        AppliedDiscount discount = new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("10.00"),
            "cart",
            "Test discount"
        );
        PriceSummary summary = new PriceSummary(
            Money.euros("100.00"),
            Money.euros("10.00"),
            Money.euros("90.00"),
            List.of(discount)
        );

        assertThatThrownBy(() -> summary.appliedDiscounts().add(discount))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}

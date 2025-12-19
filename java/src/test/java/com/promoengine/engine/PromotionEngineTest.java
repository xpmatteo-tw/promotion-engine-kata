// ABOUTME: Unit tests for PromotionEngine.
// ABOUTME: Verifies promotion orchestration and pricing calculation.
package com.promoengine.engine;

import com.promoengine.domain.*;
import com.promoengine.pricing.*;
import com.promoengine.promotions.StubPromotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PromotionEngineTest {

    private PricingContext context;
    private Product productA;
    private Cart emptyCart;
    private Cart singleItemCart;

    @BeforeEach
    void setUp() {
        context = new PricingContext(
            Instant.parse("2025-12-18T10:00:00Z"),
            "online",
            "CUST001",
            Set.of("regular")
        );

        productA = new Product(
            new Sku("SKU-A"),
            "Product A",
            "electronics"
        );

        emptyCart = new Cart(List.of());

        singleItemCart = new Cart(List.of(
            new LineItem(productA, new Quantity(2), Money.euros("10.00"))
        ));
    }

    @Test
    void shouldCalculateSubtotalWithoutPromotions() {
        PromotionEngine engine = new PromotionEngine(List.of());
        PriceSummary summary = engine.price(singleItemCart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("0.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.appliedDiscounts()).isEmpty();
    }

    @Test
    void shouldApplyApplicablePromotion() {
        AppliedDiscount discount = new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("2.00"),
            "line",
            "Test discount"
        );

        StubPromotion promotion = new StubPromotion(
            new PromotionId("PROMO-123"),
            true,  // applicable
            List.of(discount)
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion));
        PriceSummary summary = engine.price(singleItemCart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("2.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("18.00"));
        assertThat(summary.appliedDiscounts()).hasSize(1);
    }

    @Test
    void shouldNotApplyNonApplicablePromotion() {
        StubPromotion promotion = new StubPromotion(
            new PromotionId("PROMO-123"),
            false,  // not applicable
            List.of()
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion));
        PriceSummary summary = engine.price(singleItemCart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("0.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.appliedDiscounts()).isEmpty();
    }

    @Test
    void shouldApplyMultiplePromotions() {
        AppliedDiscount discount1 = new AppliedDiscount(
            new PromotionId("PROMO-1"),
            Money.euros("2.00"),
            "line",
            "First discount"
        );

        AppliedDiscount discount2 = new AppliedDiscount(
            new PromotionId("PROMO-2"),
            Money.euros("3.00"),
            "line",
            "Second discount"
        );

        StubPromotion promotion1 = new StubPromotion(
            new PromotionId("PROMO-1"),
            true,
            List.of(discount1)
        );

        StubPromotion promotion2 = new StubPromotion(
            new PromotionId("PROMO-2"),
            true,
            List.of(discount2)
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion1, promotion2));
        PriceSummary summary = engine.price(singleItemCart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("5.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("15.00"));
        assertThat(summary.appliedDiscounts()).hasSize(2);
    }

    @Test
    void shouldHandlePromotionWithMultipleDiscounts() {
        AppliedDiscount discount1 = new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("2.00"),
            "line",
            "First part"
        );

        AppliedDiscount discount2 = new AppliedDiscount(
            new PromotionId("PROMO-123"),
            Money.euros("3.00"),
            "line",
            "Second part"
        );

        StubPromotion promotion = new StubPromotion(
            new PromotionId("PROMO-123"),
            true,
            List.of(discount1, discount2)
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion));
        PriceSummary summary = engine.price(singleItemCart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("5.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("15.00"));
        assertThat(summary.appliedDiscounts()).hasSize(2);
    }

    @Test
    void shouldHandleEmptyCart() {
        StubPromotion promotion = new StubPromotion(
            new PromotionId("PROMO-123"),
            true,
            List.of()
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion));
        PriceSummary summary = engine.price(emptyCart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("0.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("0.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("0.00"));
    }

    @Test
    void shouldRejectNullPromotions() {
        assertThatThrownBy(() -> new PromotionEngine(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Promotions cannot be null");
    }

    @Test
    void shouldRejectNullCart() {
        PromotionEngine engine = new PromotionEngine(List.of());
        assertThatThrownBy(() -> engine.price(null, context))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cart cannot be null");
    }

    @Test
    void shouldRejectNullContext() {
        PromotionEngine engine = new PromotionEngine(List.of());
        assertThatThrownBy(() -> engine.price(singleItemCart, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Context cannot be null");
    }
}

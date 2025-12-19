// ABOUTME: Acceptance tests for the promotion engine system.
// ABOUTME: Tests end-to-end behavior of pricing carts with promotions.
package com.promoengine.engine;

import com.promoengine.domain.*;
import com.promoengine.pricing.*;
import com.promoengine.promotions.StubPromotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class AcceptanceTest {

    private PricingContext context;
    private Product productA;
    private Product productB;
    private Product productC;

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

        productB = new Product(
            new Sku("SKU-B"),
            "Product B",
            "electronics"
        );

        productC = new Product(
            new Sku("SKU-C"),
            "Product C",
            "books"
        );
    }

    @Test
    void shouldCalculateSubtotalWithoutPromotions() {
        Cart cart = new Cart(List.of(
            new LineItem(productA, new Quantity(2), Money.euros("10.00")),
            new LineItem(productB, new Quantity(1), Money.euros("25.00"))
        ));

        PromotionEngine engine = new PromotionEngine(List.of());
        PriceSummary summary = engine.price(cart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("45.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("0.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("45.00"));
        assertThat(summary.appliedDiscounts()).isEmpty();
    }

    @Test
    void shouldApplySinglePromotion() {
        Cart cart = new Cart(List.of(
            new LineItem(productA, new Quantity(2), Money.euros("10.00"))
        ));

        AppliedDiscount discount = new AppliedDiscount(
            new PromotionId("PROMO-10-OFF-A"),
            Money.euros("2.00"),
            "line",
            "$2 off SKU-A",
            Map.of(new Sku("SKU-A"), Money.euros("2.00"))
        );

        StubPromotion promotion = new StubPromotion(
            new PromotionId("PROMO-10-OFF-A"),
            true,
            List.of(discount)
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion));
        PriceSummary summary = engine.price(cart, context);

        // 2 * $10.00 = $20.00, $2 off = $18.00
        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("2.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("18.00"));
        assertThat(summary.appliedDiscounts()).hasSize(1);

        AppliedDiscount appliedDiscount = summary.appliedDiscounts().get(0);
        assertThat(appliedDiscount.promotionId()).isEqualTo(new PromotionId("PROMO-10-OFF-A"));
        assertThat(appliedDiscount.amount()).isEqualTo(Money.euros("2.00"));
        assertThat(appliedDiscount.target()).isEqualTo("line");
        assertThat(appliedDiscount.details()).isEqualTo("$2 off SKU-A");
    }

    @Test
    void shouldApplyMultiplePromotions() {
        Cart cart = new Cart(List.of(
            new LineItem(productA, new Quantity(2), Money.euros("10.00")),
            new LineItem(productB, new Quantity(1), Money.euros("25.00"))
        ));

        AppliedDiscount discount1 = new AppliedDiscount(
            new PromotionId("PROMO-10-OFF-A"),
            Money.euros("2.00"),
            "line",
            "10% off SKU-A"
        );

        AppliedDiscount discount2 = new AppliedDiscount(
            new PromotionId("PROMO-5-OFF-B"),
            Money.euros("5.00"),
            "line",
            "$5 off SKU-B"
        );

        StubPromotion promotion1 = new StubPromotion(
            new PromotionId("PROMO-10-OFF-A"),
            true,
            List.of(discount1)
        );

        StubPromotion promotion2 = new StubPromotion(
            new PromotionId("PROMO-5-OFF-B"),
            true,
            List.of(discount2)
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion1, promotion2));
        PriceSummary summary = engine.price(cart, context);

        // Subtotal: 2*$10 + 1*$25 = $45
        // Discounts: $2 + $5 = $7
        // Total: $45 - $7 = $38
        assertThat(summary.subtotal()).isEqualTo(Money.euros("45.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("7.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("38.00"));
        assertThat(summary.appliedDiscounts()).hasSize(2);
    }

    @Test
    void shouldNotApplyNonApplicablePromotions() {
        Cart cart = new Cart(List.of(
            new LineItem(productA, new Quantity(2), Money.euros("10.00"))
        ));

        StubPromotion nonApplicablePromotion = new StubPromotion(
            new PromotionId("PROMO-NOT-APPLICABLE"),
            false,  // Not applicable
            List.of()
        );

        PromotionEngine engine = new PromotionEngine(List.of(nonApplicablePromotion));
        PriceSummary summary = engine.price(cart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("0.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("20.00"));
        assertThat(summary.appliedDiscounts()).isEmpty();
    }

    @Test
    void shouldPreserveExplainabilityWithAllocations() {
        Cart cart = new Cart(List.of(
            new LineItem(productA, new Quantity(2), Money.euros("10.00")),
            new LineItem(productB, new Quantity(1), Money.euros("25.00"))
        ));

        Map<Sku, Money> allocations = Map.of(
            new Sku("SKU-A"), Money.euros("1.50"),
            new Sku("SKU-B"), Money.euros("2.50")
        );

        AppliedDiscount discount = new AppliedDiscount(
            new PromotionId("PROMO-MULTI-PRODUCT"),
            Money.euros("4.00"),
            "cart",
            "Multi-product discount",
            allocations
        );

        StubPromotion promotion = new StubPromotion(
            new PromotionId("PROMO-MULTI-PRODUCT"),
            true,
            List.of(discount)
        );

        PromotionEngine engine = new PromotionEngine(List.of(promotion));
        PriceSummary summary = engine.price(cart, context);

        assertThat(summary.subtotal()).isEqualTo(Money.euros("45.00"));
        assertThat(summary.discountTotal()).isEqualTo(Money.euros("4.00"));
        assertThat(summary.total()).isEqualTo(Money.euros("41.00"));

        AppliedDiscount appliedDiscount = summary.appliedDiscounts().get(0);
        assertThat(appliedDiscount.allocations()).hasSize(2);
        assertThat(appliedDiscount.allocations().get(new Sku("SKU-A"))).isEqualTo(Money.euros("1.50"));
        assertThat(appliedDiscount.allocations().get(new Sku("SKU-B"))).isEqualTo(Money.euros("2.50"));
    }
}

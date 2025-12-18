# ABOUTME: Acceptance tests for the promotion engine pricing system
# ABOUTME: Defines complete user-facing behavior for cart pricing with promotions

import unittest
from datetime import datetime, timezone
from decimal import Decimal

from promo_engine.domain import (
    Money, Percentage, Quantity, Sku, PromotionId,
    Product, LineItem, Cart, PricingContext, PriceSummary, AppliedDiscount
)
from promo_engine.promotions import Promotion
from promo_engine.engine import PromotionEngine


# Stub promotions for testing
class StubPromotion(Promotion):
    """A stub promotion for testing that can be configured with specific behavior."""

    def __init__(self, promo_id: PromotionId, applicable: bool = True, discounts: list[AppliedDiscount] = None):
        self._id = promo_id
        self._applicable = applicable
        self._discounts = discounts or []

    @property
    def id(self) -> PromotionId:
        return self._id

    def is_applicable(self, cart: Cart, context: PricingContext) -> bool:
        return self._applicable

    def apply(self, cart: Cart, context: PricingContext) -> list[AppliedDiscount]:
        return self._discounts


class TestPromotionEngine(unittest.TestCase):
    """Acceptance tests defining complete pricing behavior."""

    def setUp(self):
        """Set up common test fixtures."""
        self.context = PricingContext(
            now=datetime.now(timezone.utc),
            channel="online",
            customer_id="CUST001",
            customer_tags={"regular"}
        )

        # Common products
        self.product_a = Product(
            sku=Sku("SKU-A"),
            name="Product A",
            category="electronics"
        )
        self.product_b = Product(
            sku=Sku("SKU-B"),
            name="Product B",
            category="electronics"
        )
        self.product_c = Product(
            sku=Sku("SKU-C"),
            name="Product C",
            category="books"
        )

    def test_subtotal_without_promotions(self):
        """Calculate subtotal and total when no promotions are configured."""
        cart = Cart([
            LineItem(self.product_a, Quantity(2), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("25.00")))
        ])

        engine = PromotionEngine([])
        summary = engine.price(cart, self.context)

        self.assertEqual(summary.subtotal, Money(Decimal("45.00")))
        self.assertEqual(summary.discount_total, Money(Decimal("0.00")))
        self.assertEqual(summary.total, Money(Decimal("45.00")))
        self.assertEqual(len(summary.applied_discounts), 0)

    def test_single_applicable_promotion(self):
        """Apply a single applicable promotion to a cart."""
        cart = Cart([
            LineItem(self.product_a, Quantity(2), Money(Decimal("10.00")))
        ])

        discount = AppliedDiscount(
            promotion_id=PromotionId("PROMO-10-OFF-A"),
            amount=Money(Decimal("2.00")),
            target="line",
            details="$2 off SKU-A",
            allocations={Sku("SKU-A"): Money(Decimal("2.00"))}
        )

        promotion = StubPromotion(
            promo_id=PromotionId("PROMO-10-OFF-A"),
            applicable=True,
            discounts=[discount]
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        # 2 * $10.00 = $20.00, $2 off = $18.00
        self.assertEqual(summary.subtotal, Money(Decimal("20.00")))
        self.assertEqual(summary.discount_total, Money(Decimal("2.00")))
        self.assertEqual(summary.total, Money(Decimal("18.00")))
        self.assertEqual(len(summary.applied_discounts), 1)

        applied_discount = summary.applied_discounts[0]
        self.assertEqual(applied_discount.promotion_id, PromotionId("PROMO-10-OFF-A"))
        self.assertEqual(applied_discount.amount, Money(Decimal("2.00")))
        self.assertEqual(applied_discount.target, "line")

    def test_multiple_lines_with_allocations(self):
        """Apply promotion with allocations across multiple line items."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("20.00"))),
            LineItem(self.product_c, Quantity(1), Money(Decimal("15.00")))
        ])

        # Promotion with allocations to SKU-A and SKU-B
        discount = AppliedDiscount(
            promotion_id=PromotionId("PROMO-15-OFF-ELECTRONICS"),
            amount=Money(Decimal("4.50")),
            target="line",
            details="$4.50 off electronics",
            allocations={
                Sku("SKU-A"): Money(Decimal("1.50")),
                Sku("SKU-B"): Money(Decimal("3.00"))
            }
        )

        promotion = StubPromotion(
            promo_id=PromotionId("PROMO-15-OFF-ELECTRONICS"),
            applicable=True,
            discounts=[discount]
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        # Subtotal: $10 + $20 + $15 = $45
        # Discount: $1.50 + $3.00 = $4.50
        # SKU-C is not in allocations
        self.assertEqual(summary.subtotal, Money(Decimal("45.00")))
        self.assertEqual(summary.discount_total, Money(Decimal("4.50")))
        self.assertEqual(summary.total, Money(Decimal("40.50")))
        self.assertEqual(len(summary.applied_discounts), 1)

        applied_discount = summary.applied_discounts[0]
        self.assertIsNotNone(applied_discount.allocations)
        self.assertEqual(len(applied_discount.allocations), 2)
        self.assertIn(Sku("SKU-A"), applied_discount.allocations)
        self.assertIn(Sku("SKU-B"), applied_discount.allocations)
        self.assertEqual(applied_discount.allocations[Sku("SKU-A")], Money(Decimal("1.50")))
        self.assertEqual(applied_discount.allocations[Sku("SKU-B")], Money(Decimal("3.00")))

    def test_explainability(self):
        """Verify that applied discounts contain promotion ID and readable details."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("50.00")))
        ])

        discount = AppliedDiscount(
            promotion_id=PromotionId("SUMMER-SALE-2025"),
            amount=Money(Decimal("10.00")),
            target="line",
            details="Summer sale: $10 off SKU-A",
            allocations={Sku("SKU-A"): Money(Decimal("10.00"))}
        )

        promotion = StubPromotion(
            promo_id=PromotionId("SUMMER-SALE-2025"),
            applicable=True,
            discounts=[discount]
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        self.assertEqual(len(summary.applied_discounts), 1)
        applied_discount = summary.applied_discounts[0]

        # Verify explainability
        self.assertEqual(applied_discount.promotion_id, PromotionId("SUMMER-SALE-2025"))
        self.assertIsNotNone(applied_discount.details)
        self.assertIn("SKU-A", applied_discount.details)  # SKU appears in details
        self.assertIsNotNone(applied_discount.allocations)
        self.assertEqual(applied_discount.allocations[Sku("SKU-A")], Money(Decimal("10.00")))

    def test_multiple_promotions(self):
        """Verify that multiple promotions can be applied and their discounts are summed."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("20.00"))),
            LineItem(self.product_c, Quantity(1), Money(Decimal("15.00")))
        ])

        # First promotion gives $2.50 off
        discount1 = AppliedDiscount(
            promotion_id=PromotionId("PROMO-1"),
            amount=Money(Decimal("2.50")),
            target="line",
            details="Promotion 1",
            allocations={Sku("SKU-A"): Money(Decimal("2.50"))}
        )

        # Second promotion gives $3.25 off
        discount2 = AppliedDiscount(
            promotion_id=PromotionId("PROMO-2"),
            amount=Money(Decimal("3.25")),
            target="line",
            details="Promotion 2",
            allocations={Sku("SKU-B"): Money(Decimal("3.25"))}
        )

        promotion1 = StubPromotion(
            promo_id=PromotionId("PROMO-1"),
            applicable=True,
            discounts=[discount1]
        )

        promotion2 = StubPromotion(
            promo_id=PromotionId("PROMO-2"),
            applicable=True,
            discounts=[discount2]
        )

        engine = PromotionEngine([promotion1, promotion2])
        summary = engine.price(cart, self.context)

        # Total discount: $2.50 + $3.25 = $5.75
        self.assertEqual(summary.subtotal, Money(Decimal("45.00")))
        self.assertEqual(summary.discount_total, Money(Decimal("5.75")))
        self.assertEqual(summary.total, Money(Decimal("39.25")))
        self.assertEqual(len(summary.applied_discounts), 2)


if __name__ == '__main__':
    unittest.main()

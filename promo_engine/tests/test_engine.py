# ABOUTME: Acceptance tests for the promotion engine pricing system
# ABOUTME: Defines complete user-facing behavior for cart pricing with promotions

import unittest
from datetime import datetime, timezone
from decimal import Decimal

from promo_engine.domain import (
    Money, Percentage, Quantity, Sku, PromotionId,
    Product, LineItem, Cart, PricingContext, PriceSummary
)
from promo_engine.promotions import PercentOffProductPromotion
from promo_engine.engine import PromotionEngine


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

    def test_percent_off_single_line(self):
        """Apply percentage discount to a single line item."""
        cart = Cart([
            LineItem(self.product_a, Quantity(2), Money(Decimal("10.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-10-OFF-A"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("10"))
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        # 2 * $10.00 = $20.00, 10% off = $2.00
        self.assertEqual(summary.subtotal, Money(Decimal("20.00")))
        self.assertEqual(summary.discount_total, Money(Decimal("2.00")))
        self.assertEqual(summary.total, Money(Decimal("18.00")))
        self.assertEqual(len(summary.applied_discounts), 1)

        discount = summary.applied_discounts[0]
        self.assertEqual(discount.promotion_id, PromotionId("PROMO-10-OFF-A"))
        self.assertEqual(discount.amount, Money(Decimal("2.00")))
        self.assertEqual(discount.target, "line")

    def test_percent_off_multiple_lines_partial_eligible(self):
        """Apply promotion to cart with multiple items where only some are eligible."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("20.00"))),
            LineItem(self.product_c, Quantity(1), Money(Decimal("15.00")))
        ])

        # Promotion applies only to SKU-A and SKU-B
        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-15-OFF-ELECTRONICS"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B")},
            percentage=Percentage(Decimal("15"))
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        # Subtotal: $10 + $20 + $15 = $45
        # Discount: ($10 * 0.15) + ($20 * 0.15) = $1.50 + $3.00 = $4.50
        # SKU-C is not eligible, no discount
        self.assertEqual(summary.subtotal, Money(Decimal("45.00")))
        self.assertEqual(summary.discount_total, Money(Decimal("4.50")))
        self.assertEqual(summary.total, Money(Decimal("40.50")))
        self.assertEqual(len(summary.applied_discounts), 1)

        discount = summary.applied_discounts[0]
        self.assertIsNotNone(discount.allocations)
        self.assertEqual(len(discount.allocations), 2)
        self.assertIn(Sku("SKU-A"), discount.allocations)
        self.assertIn(Sku("SKU-B"), discount.allocations)
        self.assertEqual(discount.allocations[Sku("SKU-A")], Money(Decimal("1.50")))
        self.assertEqual(discount.allocations[Sku("SKU-B")], Money(Decimal("3.00")))

    def test_explainability(self):
        """Verify that applied discounts contain promotion ID and readable details."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("50.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("SUMMER-SALE-2025"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("20"))
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        self.assertEqual(len(summary.applied_discounts), 1)
        discount = summary.applied_discounts[0]

        # Verify explainability
        self.assertEqual(discount.promotion_id, PromotionId("SUMMER-SALE-2025"))
        self.assertIsNotNone(discount.details)
        self.assertIn("20", discount.details)  # Percentage appears in details
        self.assertIn("SKU-A", discount.details)  # SKU appears in details
        self.assertIsNotNone(discount.allocations)
        self.assertEqual(discount.allocations[Sku("SKU-A")], Money(Decimal("10.00")))

    def test_rounding_per_line(self):
        """Verify that rounding happens per line, then discounts are summed."""
        cart = Cart([
            # $10.00 * 15% = $1.50 (no rounding needed)
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            # $10.01 * 15% = $1.5015 -> rounds to $1.50
            LineItem(self.product_b, Quantity(1), Money(Decimal("10.01"))),
            # $10.07 * 15% = $1.5105 -> rounds to $1.51
            LineItem(self.product_c, Quantity(1), Money(Decimal("10.07")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-15-OFF"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B"), Sku("SKU-C")},
            percentage=Percentage(Decimal("15"))
        )

        engine = PromotionEngine([promotion])
        summary = engine.price(cart, self.context)

        # Each line rounded individually:
        # $1.50 + $1.50 + $1.51 = $4.51
        self.assertEqual(summary.discount_total, Money(Decimal("4.51")))

        discount = summary.applied_discounts[0]
        self.assertEqual(discount.allocations[Sku("SKU-A")], Money(Decimal("1.50")))
        self.assertEqual(discount.allocations[Sku("SKU-B")], Money(Decimal("1.50")))
        self.assertEqual(discount.allocations[Sku("SKU-C")], Money(Decimal("1.51")))


if __name__ == '__main__':
    unittest.main()

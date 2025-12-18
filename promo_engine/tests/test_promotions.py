# ABOUTME: Unit tests for promotion implementations
# ABOUTME: Tests promotion applicability and discount calculation logic

import unittest
from datetime import datetime, timezone
from decimal import Decimal

from promo_engine.domain import (
    Money, Percentage, Quantity, Sku, PromotionId,
    Product, LineItem, Cart, PricingContext
)
from promo_engine.promotions import PercentOffProductPromotion


class TestPercentOffProductPromotion(unittest.TestCase):
    """Test PercentOffProductPromotion behavior."""

    def setUp(self):
        """Set up common test fixtures."""
        self.context = PricingContext(
            now=datetime.now(timezone.utc),
            channel="online",
            customer_id="CUST001",
            customer_tags={"regular"}
        )

        self.product_a = Product(Sku("SKU-A"), "Product A", "electronics")
        self.product_b = Product(Sku("SKU-B"), "Product B", "electronics")
        self.product_c = Product(Sku("SKU-C"), "Product C", "books")

    def test_is_applicable_with_eligible_sku(self):
        """Promotion should be applicable when cart contains eligible SKU."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-1"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("10"))
        )

        self.assertTrue(promotion.is_applicable(cart, self.context))

    def test_is_applicable_without_eligible_sku(self):
        """Promotion should not be applicable when cart has no eligible SKUs."""
        cart = Cart([
            LineItem(self.product_c, Quantity(1), Money(Decimal("10.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-1"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B")},
            percentage=Percentage(Decimal("10"))
        )

        self.assertFalse(promotion.is_applicable(cart, self.context))

    def test_is_applicable_with_mixed_cart(self):
        """Promotion should be applicable if at least one eligible SKU present."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_c, Quantity(1), Money(Decimal("15.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-1"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("10"))
        )

        self.assertTrue(promotion.is_applicable(cart, self.context))

    def test_apply_single_eligible_line(self):
        """Apply discount to single eligible line."""
        cart = Cart([
            LineItem(self.product_a, Quantity(2), Money(Decimal("10.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-10"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("10"))
        )

        discounts = promotion.apply(cart, self.context)

        self.assertEqual(len(discounts), 1)
        discount = discounts[0]

        # 2 * $10.00 * 0.10 = $2.00
        self.assertEqual(discount.amount, Money(Decimal("2.00")))
        self.assertEqual(discount.promotion_id, PromotionId("PROMO-10"))
        self.assertEqual(discount.target, "line")

    def test_apply_multiple_eligible_lines(self):
        """Apply discount to multiple eligible lines."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("20.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-15"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B")},
            percentage=Percentage(Decimal("15"))
        )

        discounts = promotion.apply(cart, self.context)

        self.assertEqual(len(discounts), 1)
        discount = discounts[0]

        # ($10.00 * 0.15) + ($20.00 * 0.15) = $1.50 + $3.00 = $4.50
        self.assertEqual(discount.amount, Money(Decimal("4.50")))

    def test_apply_mixed_cart(self):
        """Apply discount only to eligible lines in mixed cart."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("20.00"))),
            LineItem(self.product_c, Quantity(1), Money(Decimal("15.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-20"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B")},
            percentage=Percentage(Decimal("20"))
        )

        discounts = promotion.apply(cart, self.context)

        self.assertEqual(len(discounts), 1)
        discount = discounts[0]

        # ($10.00 * 0.20) + ($20.00 * 0.20) = $2.00 + $4.00 = $6.00
        # SKU-C should not be discounted
        self.assertEqual(discount.amount, Money(Decimal("6.00")))

    def test_apply_returns_empty_when_no_eligible(self):
        """Apply should return empty list when no eligible SKUs in cart."""
        cart = Cart([
            LineItem(self.product_c, Quantity(1), Money(Decimal("10.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-10"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("10"))
        )

        discounts = promotion.apply(cart, self.context)
        self.assertEqual(len(discounts), 0)

    def test_allocations_populated_correctly(self):
        """Allocations dict should map SKU to discount amount."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("30.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-10"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B")},
            percentage=Percentage(Decimal("10"))
        )

        discounts = promotion.apply(cart, self.context)
        discount = discounts[0]

        self.assertIsNotNone(discount.allocations)
        self.assertEqual(len(discount.allocations), 2)
        self.assertEqual(discount.allocations[Sku("SKU-A")], Money(Decimal("1.00")))
        self.assertEqual(discount.allocations[Sku("SKU-B")], Money(Decimal("3.00")))

    def test_details_string_format(self):
        """Details string should contain percentage and SKU information."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-25"),
            eligible_skus={Sku("SKU-A")},
            percentage=Percentage(Decimal("25"))
        )

        discounts = promotion.apply(cart, self.context)
        discount = discounts[0]

        self.assertIn("25", discount.details)
        self.assertIn("SKU-A", discount.details)

    def test_details_with_multiple_skus(self):
        """Details string should list all eligible SKUs."""
        cart = Cart([
            LineItem(self.product_a, Quantity(1), Money(Decimal("10.00"))),
            LineItem(self.product_b, Quantity(1), Money(Decimal("20.00")))
        ])

        promotion = PercentOffProductPromotion(
            _id=PromotionId("PROMO-15"),
            eligible_skus={Sku("SKU-A"), Sku("SKU-B")},
            percentage=Percentage(Decimal("15"))
        )

        discounts = promotion.apply(cart, self.context)
        discount = discounts[0]

        self.assertIn("SKU-A", discount.details)
        self.assertIn("SKU-B", discount.details)


if __name__ == '__main__':
    unittest.main()

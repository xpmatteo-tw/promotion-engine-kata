# ABOUTME: Unit tests for domain value types and model
# ABOUTME: Tests Money arithmetic, validation, and domain model behavior

import unittest
from decimal import Decimal

from promo_engine.domain import (
    Money, Percentage, Quantity, Sku, PromotionId,
    Product, LineItem, Cart
)


class TestMoney(unittest.TestCase):
    """Test Money value type."""

    def test_money_quantizes_on_construction(self):
        """Money should quantize to 2 decimals on construction."""
        money = Money(Decimal("10.12345"))
        self.assertEqual(money.amount, Decimal("10.12"))

    def test_money_rounding_half_up(self):
        """Money should use ROUND_HALF_UP for quantization."""
        # .235 rounds up to .24
        self.assertEqual(Money(Decimal("1.235")).amount, Decimal("1.24"))
        # .225 rounds up to .23 (half-up means .5 rounds away from zero)
        self.assertEqual(Money(Decimal("1.225")).amount, Decimal("1.23"))
        # .224 rounds down to .22
        self.assertEqual(Money(Decimal("1.224")).amount, Decimal("1.22"))

    def test_money_addition(self):
        """Money addition should return properly quantized Money."""
        m1 = Money(Decimal("10.50"))
        m2 = Money(Decimal("5.25"))
        result = m1 + m2
        self.assertEqual(result, Money(Decimal("15.75")))
        self.assertIsInstance(result, Money)

    def test_money_subtraction(self):
        """Money subtraction should return properly quantized Money."""
        m1 = Money(Decimal("10.50"))
        m2 = Money(Decimal("5.25"))
        result = m1 - m2
        self.assertEqual(result, Money(Decimal("5.25")))
        self.assertIsInstance(result, Money)

    def test_money_multiplication(self):
        """Money multiplication by Decimal should return quantized Money."""
        m = Money(Decimal("10.00"))
        result = m * Decimal("0.15")
        self.assertEqual(result, Money(Decimal("1.50")))
        self.assertIsInstance(result, Money)

    def test_money_multiplication_with_rounding(self):
        """Money multiplication should quantize the result."""
        m = Money(Decimal("10.01"))
        result = m * Decimal("0.15")
        # 10.01 * 0.15 = 1.5015, rounds to 1.50
        self.assertEqual(result, Money(Decimal("1.50")))

    def test_money_comparisons(self):
        """Money should support comparison operators."""
        m1 = Money(Decimal("10.00"))
        m2 = Money(Decimal("20.00"))
        m3 = Money(Decimal("10.00"))

        self.assertTrue(m1 < m2)
        self.assertTrue(m1 <= m2)
        self.assertTrue(m1 <= m3)
        self.assertTrue(m2 > m1)
        self.assertTrue(m2 >= m1)
        self.assertTrue(m1 >= m3)
        self.assertTrue(m1 == m3)
        self.assertFalse(m1 == m2)

    def test_money_str(self):
        """Money string representation should include dollar sign."""
        m = Money(Decimal("42.50"))
        self.assertEqual(str(m), "$42.50")


class TestPercentage(unittest.TestCase):
    """Test Percentage value type."""

    def test_percentage_valid_range(self):
        """Percentage should accept values between 0 and 100."""
        p0 = Percentage(Decimal("0"))
        p50 = Percentage(Decimal("50"))
        p100 = Percentage(Decimal("100"))
        self.assertEqual(p0.value, Decimal("0"))
        self.assertEqual(p50.value, Decimal("50"))
        self.assertEqual(p100.value, Decimal("100"))

    def test_percentage_rejects_negative(self):
        """Percentage should reject negative values."""
        with self.assertRaises(ValueError) as ctx:
            Percentage(Decimal("-1"))
        self.assertIn("between 0 and 100", str(ctx.exception))

    def test_percentage_rejects_over_100(self):
        """Percentage should reject values over 100."""
        with self.assertRaises(ValueError) as ctx:
            Percentage(Decimal("101"))
        self.assertIn("between 0 and 100", str(ctx.exception))

    def test_percentage_as_decimal(self):
        """as_decimal should convert percentage to decimal for calculations."""
        p15 = Percentage(Decimal("15"))
        self.assertEqual(p15.as_decimal(), Decimal("0.15"))

        p50 = Percentage(Decimal("50"))
        self.assertEqual(p50.as_decimal(), Decimal("0.5"))

        p100 = Percentage(Decimal("100"))
        self.assertEqual(p100.as_decimal(), Decimal("1"))

    def test_percentage_str(self):
        """Percentage string representation should include percent sign."""
        p = Percentage(Decimal("25"))
        self.assertEqual(str(p), "25%")


class TestQuantity(unittest.TestCase):
    """Test Quantity value type."""

    def test_quantity_non_negative(self):
        """Quantity should accept non-negative integers."""
        q0 = Quantity(0)
        q5 = Quantity(5)
        self.assertEqual(q0.value, 0)
        self.assertEqual(q5.value, 5)

    def test_quantity_rejects_negative(self):
        """Quantity should reject negative values."""
        with self.assertRaises(ValueError) as ctx:
            Quantity(-1)
        self.assertIn(">= 0", str(ctx.exception))

    def test_quantity_int_conversion(self):
        """Quantity should convert to int."""
        q = Quantity(42)
        self.assertEqual(int(q), 42)


class TestSku(unittest.TestCase):
    """Test Sku value type."""

    def test_sku_non_empty(self):
        """Sku should accept non-empty strings."""
        sku = Sku("SKU-123")
        self.assertEqual(sku.value, "SKU-123")
        self.assertEqual(str(sku), "SKU-123")

    def test_sku_rejects_empty(self):
        """Sku should reject empty strings."""
        with self.assertRaises(ValueError) as ctx:
            Sku("")
        self.assertIn("cannot be empty", str(ctx.exception))

    def test_sku_hashable(self):
        """Sku should be hashable for use in sets and dicts."""
        sku1 = Sku("SKU-A")
        sku2 = Sku("SKU-A")
        sku3 = Sku("SKU-B")

        # Same value should hash the same
        self.assertEqual(hash(sku1), hash(sku2))
        # Can use in sets
        sku_set = {sku1, sku2, sku3}
        self.assertEqual(len(sku_set), 2)  # sku1 and sku2 are duplicates


class TestPromotionId(unittest.TestCase):
    """Test PromotionId value type."""

    def test_promotion_id_non_empty(self):
        """PromotionId should accept non-empty strings."""
        pid = PromotionId("PROMO-123")
        self.assertEqual(pid.value, "PROMO-123")
        self.assertEqual(str(pid), "PROMO-123")

    def test_promotion_id_rejects_empty(self):
        """PromotionId should reject empty strings."""
        with self.assertRaises(ValueError) as ctx:
            PromotionId("")
        self.assertIn("cannot be empty", str(ctx.exception))


class TestLineItem(unittest.TestCase):
    """Test LineItem domain model."""

    def test_line_item_subtotal(self):
        """LineItem.subtotal should multiply unit_price by quantity."""
        product = Product(Sku("SKU-A"), "Product A", "category")
        line = LineItem(product, Quantity(3), Money(Decimal("10.00")))

        subtotal = line.subtotal()
        self.assertEqual(subtotal, Money(Decimal("30.00")))

    def test_line_item_subtotal_with_rounding(self):
        """LineItem.subtotal should properly round the result."""
        product = Product(Sku("SKU-A"), "Product A", "category")
        line = LineItem(product, Quantity(3), Money(Decimal("10.01")))

        subtotal = line.subtotal()
        # 3 * 10.01 = 30.03
        self.assertEqual(subtotal, Money(Decimal("30.03")))


class TestCart(unittest.TestCase):
    """Test Cart domain model."""

    def test_cart_empty_subtotal(self):
        """Empty cart should have zero subtotal."""
        cart = Cart([])
        self.assertEqual(cart.subtotal(), Money(Decimal("0.00")))

    def test_cart_single_line_subtotal(self):
        """Cart with single line should return that line's subtotal."""
        product = Product(Sku("SKU-A"), "Product A", "category")
        line = LineItem(product, Quantity(2), Money(Decimal("10.00")))
        cart = Cart([line])

        self.assertEqual(cart.subtotal(), Money(Decimal("20.00")))

    def test_cart_multiple_lines_subtotal(self):
        """Cart with multiple lines should sum all line subtotals."""
        product_a = Product(Sku("SKU-A"), "Product A", "category")
        product_b = Product(Sku("SKU-B"), "Product B", "category")

        line1 = LineItem(product_a, Quantity(2), Money(Decimal("10.00")))
        line2 = LineItem(product_b, Quantity(1), Money(Decimal("25.00")))

        cart = Cart([line1, line2])

        # 20.00 + 25.00 = 45.00
        self.assertEqual(cart.subtotal(), Money(Decimal("45.00")))


if __name__ == '__main__':
    unittest.main()

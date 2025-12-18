# ABOUTME: Core pricing engine that applies promotions to shopping carts
# ABOUTME: Orchestrates promotion evaluation and generates price summaries

from decimal import Decimal

from promo_engine.domain import Cart, PricingContext, PriceSummary, Money
from promo_engine.promotions import Promotion


# TODO: Add promotion priority/ordering support
# TODO: Add promotion combination rules (stackable vs exclusive)
# TODO: Consider tracking which promotions were evaluated but not applicable


class PromotionEngine:
    """Main pricing engine that applies promotions to carts."""

    def __init__(self, promotions: list[Promotion]):
        """
        Initialize with a list of available promotions.

        Args:
            promotions: List of promotion instances to consider when pricing
        """
        self.promotions = promotions

    def price(self, cart: Cart, context: PricingContext) -> PriceSummary:
        """
        Calculate final price for a cart with applicable promotions.

        Algorithm:
        1. Calculate cart subtotal
        2. Find all applicable promotions
        3. Apply each promotion to get discounts
        4. Sum all discounts
        5. Calculate final total
        6. Return complete summary with explainability

        Args:
            cart: Shopping cart to price
            context: Pricing context (time, channel, customer info)

        Returns:
            PriceSummary with subtotal, discounts, and total
        """
        subtotal = cart.subtotal()

        all_discounts = []

        for promotion in self.promotions:
            if promotion.is_applicable(cart, context):
                discounts = promotion.apply(cart, context)
                all_discounts.extend(discounts)

        discount_total = Money(Decimal('0'))
        if all_discounts:
            discount_total = sum(
                (d.amount for d in all_discounts),
                Money(Decimal('0'))
            )

        total = subtotal - discount_total

        return PriceSummary(
            subtotal=subtotal,
            discount_total=discount_total,
            total=total,
            applied_discounts=all_discounts
        )

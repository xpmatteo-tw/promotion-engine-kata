# ABOUTME: Promotion abstractions and implementations
# ABOUTME: Defines the Promotion protocol and concrete promotion types

from abc import ABC, abstractmethod
from dataclasses import dataclass
from decimal import Decimal

from promo_engine.domain import (
    Cart, PricingContext, AppliedDiscount, PromotionId,
    Sku, Percentage, Money
)


# TODO: Add time-based applicability (valid_from, valid_until)
# TODO: Add day-of-week and time-of-day restrictions
# TODO: Add customer segment targeting using context.customer_tags
# TODO: Add promotion priority/ordering for application sequence
# TODO: Add mutual exclusivity rules between promotions
# TODO: Add maximum discount caps
# TODO: Consider BuyXGetYPromotion for quantity-based discounts
# TODO: Consider FixedAmountOffPromotion
# TODO: Consider ThresholdPromotion (spend $X, save $Y)


class Promotion(ABC):
    """Abstract base class for all promotions."""

    @property
    @abstractmethod
    def id(self) -> PromotionId:
        """Unique identifier for this promotion."""
        pass

    @abstractmethod
    def is_applicable(self, cart: Cart, context: PricingContext) -> bool:
        """
        Determine if this promotion can be applied.

        Returns True if the promotion should be considered for this cart and context.
        """
        pass

    @abstractmethod
    def apply(self, cart: Cart, context: PricingContext) -> list[AppliedDiscount]:
        """
        Apply the promotion and return discount details.

        Should only be called if is_applicable returns True.
        Returns list of AppliedDiscount instances with full explainability.
        """
        pass


@dataclass
class PercentOffProductPromotion(Promotion):
    """Applies a percentage discount to specific SKUs."""

    _id: PromotionId
    eligible_skus: set[Sku]
    percentage: Percentage

    @property
    def id(self) -> PromotionId:
        return self._id

    def is_applicable(self, cart: Cart, context: PricingContext) -> bool:
        """Check if any cart lines contain eligible SKUs."""
        return any(line.product.sku in self.eligible_skus for line in cart.lines)

    def apply(self, cart: Cart, context: PricingContext) -> list[AppliedDiscount]:
        """Apply percentage discount to each eligible line."""
        allocations = {}

        for line in cart.lines:
            if line.product.sku in self.eligible_skus:
                line_subtotal = line.subtotal()
                discount_amount = line_subtotal * self.percentage.as_decimal()
                allocations[line.product.sku] = discount_amount

        if not allocations:
            return []

        total_discount = sum(allocations.values(), Money(Decimal('0')))

        sku_list = ', '.join(str(sku) for sku in sorted(allocations.keys(), key=lambda s: s.value))
        details = f"{self.percentage} off {sku_list}"

        discount = AppliedDiscount(
            promotion_id=self.id,
            amount=total_discount,
            target="line",
            details=details,
            allocations=allocations
        )

        return [discount]

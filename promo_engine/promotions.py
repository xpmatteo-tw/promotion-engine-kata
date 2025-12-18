# ABOUTME: Promotion abstractions and implementations
# ABOUTME: Defines the Promotion protocol for implementing custom promotions

from abc import ABC, abstractmethod

from promo_engine.domain import (
    Cart, PricingContext, AppliedDiscount, PromotionId
)


# TODO: Add time-based applicability (valid_from, valid_until)
# TODO: Add day-of-week and time-of-day restrictions
# TODO: Add customer segment targeting using context.customer_tags
# TODO: Add promotion priority/ordering for application sequence
# TODO: Add mutual exclusivity rules between promotions
# TODO: Add maximum discount caps
# TODO: Consider BuyXGetYPromotion for quantity-based discounts
# TODO: Consider PercentOffProductPromotion
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

# ABOUTME: Value types and core domain model for promotion engine
# ABOUTME: Provides type-safe primitives and shopping cart structures

from dataclasses import dataclass
from datetime import datetime
from decimal import Decimal, ROUND_HALF_UP
from typing import Literal, Self


@dataclass(frozen=True)
class Money:
    """Immutable money value with 2 decimal precision."""
    amount: Decimal

    def __post_init__(self):
        """Quantize amount to 2 decimals using ROUND_HALF_UP."""
        quantized = self.amount.quantize(Decimal('0.01'), rounding=ROUND_HALF_UP)
        object.__setattr__(self, 'amount', quantized)

    def __add__(self, other: Self) -> Self:
        """Add two Money values."""
        return Money(self.amount + other.amount)

    def __sub__(self, other: Self) -> Self:
        """Subtract two Money values."""
        return Money(self.amount - other.amount)

    def __mul__(self, multiplier: Decimal) -> Self:
        """Multiply Money by a Decimal multiplier."""
        return Money(self.amount * multiplier)

    def __lt__(self, other: Self) -> bool:
        return self.amount < other.amount

    def __le__(self, other: Self) -> bool:
        return self.amount <= other.amount

    def __gt__(self, other: Self) -> bool:
        return self.amount > other.amount

    def __ge__(self, other: Self) -> bool:
        return self.amount >= other.amount

    def __repr__(self) -> str:
        return f"Money(Decimal('{self.amount}'))"

    def __str__(self) -> str:
        return f"${self.amount}"


@dataclass(frozen=True)
class Percentage:
    """Percentage value between 0 and 100."""
    value: Decimal

    def __post_init__(self):
        """Validate that percentage is between 0 and 100."""
        if not (Decimal('0') <= self.value <= Decimal('100')):
            raise ValueError(f"Percentage must be between 0 and 100, got {self.value}")

    def as_decimal(self) -> Decimal:
        """Convert to decimal for calculations (e.g., 15 becomes 0.15)."""
        return self.value / Decimal('100')

    def __repr__(self) -> str:
        return f"Percentage(Decimal('{self.value}'))"

    def __str__(self) -> str:
        return f"{self.value}%"


@dataclass(frozen=True)
class Quantity:
    """Non-negative quantity of items."""
    value: int

    def __post_init__(self):
        """Validate that quantity is non-negative."""
        if self.value < 0:
            raise ValueError(f"Quantity must be >= 0, got {self.value}")

    def __int__(self) -> int:
        return self.value

    def __repr__(self) -> str:
        return f"Quantity({self.value})"


@dataclass(frozen=True)
class Sku:
    """Stock keeping unit identifier."""
    value: str

    def __post_init__(self):
        """Validate that SKU is not empty."""
        if not self.value:
            raise ValueError("Sku cannot be empty")

    def __str__(self) -> str:
        return self.value

    def __hash__(self) -> int:
        return hash(self.value)

    def __repr__(self) -> str:
        return f"Sku('{self.value}')"


@dataclass(frozen=True)
class PromotionId:
    """Promotion identifier."""
    value: str

    def __post_init__(self):
        """Validate that promotion ID is not empty."""
        if not self.value:
            raise ValueError("PromotionId cannot be empty")

    def __str__(self) -> str:
        return self.value

    def __repr__(self) -> str:
        return f"PromotionId('{self.value}')"


@dataclass(frozen=True)
class Product:
    """Product catalog entry."""
    sku: Sku
    name: str
    category: str


@dataclass(frozen=True)
class LineItem:
    """Single line in a shopping cart."""
    product: Product
    quantity: Quantity
    unit_price: Money

    def subtotal(self) -> Money:
        """Calculate line subtotal before discounts."""
        return self.unit_price * Decimal(self.quantity.value)


@dataclass
class Cart:
    """Shopping cart containing line items."""
    lines: list[LineItem]

    def subtotal(self) -> Money:
        """Calculate cart subtotal before discounts."""
        if not self.lines:
            return Money(Decimal('0'))
        return sum((line.subtotal() for line in self.lines), Money(Decimal('0')))


@dataclass(frozen=True)
class PricingContext:
    """Context information for pricing calculations."""
    now: datetime
    channel: str
    customer_id: str
    customer_tags: set[str]


@dataclass(frozen=True)
class AppliedDiscount:
    """Record of a discount that was applied."""
    promotion_id: PromotionId
    amount: Money
    target: Literal["line", "cart"]
    details: str
    allocations: dict[Sku, Money] | None = None


@dataclass(frozen=True)
class PriceSummary:
    """Complete pricing breakdown for a cart."""
    subtotal: Money
    discount_total: Money
    total: Money
    applied_discounts: list[AppliedDiscount]

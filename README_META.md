# Meta prompts

The prompts for the exercise were created with GPT 5.2 in Dec 2025 with the following prompts

```
I'm preparing a training session on AI-assisted coding and TDD.  I'm looking to prepare an interesting, challenging coding problem that is also business related.  I'm thinking to discuss a promotion engine for retail.  This has several interesting axes of complexity:
* when the promotion is applicable (from date, to date, which days and hours)
* to which products or classes of products it's applicable
* how the discount is computed: percent, fixed...
* the discount depends on quantity: 3x2...
* how discounts combine

Can you help me identify any other axes of complexity that I missed?
```

and then

```
Let's design a progressive kata. I need an initial description that i can feed an AI to create a starting point, in Python using custom types for the various values that will appear; eg we might use 

from decimal import Decimal 

class Money(Decimal): 
    pass 
    
for money. Then I need a number of additional steps, in the order that you were suggesting
```

and then 

```
Please write the various steps to a markdown doc that I can download
```

## Starting point


```
Create a Python 3 project skeleton for a “Promotion Engine Kata” focused on TDD.

Requirements:
- Use only the standard library (unittest), no external deps.
- Use custom value types for domain primitives:
  - Money based on Decimal (quantize to 2 decimals) and supports +, -, *, comparisons.
  - Percentage (0..1 as Decimal) or (0..100), but pick one and be consistent.
  - Quantity (int >= 0).
  - Sku (str wrapper).
  - PromotionId (str wrapper).
- Build small, explicit domain model types:
  - Product(sku: Sku, name: str, category: str)
  - LineItem(product: Product, quantity: Quantity, unit_price: Money)
  - Cart(lines: list[LineItem])
  - Discount application result should preserve explainability:
    - AppliedDiscount(promotion_id, amount: Money, target: "line"|"cart", details: str, allocations?: dict[Sku, Money])
  - PriceSummary(subtotal: Money, discount_total: Money, total: Money, applied_discounts: list[AppliedDiscount])

Engine:
- PromotionEngine has a method price(cart: Cart, context: PricingContext) -> PriceSummary
- PricingContext includes:
  - now: datetime (timezone-aware)
  - channel: str (e.g., "online")
  - customer_id: str
  - customer_tags: set[str] (e.g., {"new", "gold"})
- Promotions are objects with:
  - id: PromotionId
  - is_applicable(cart, context) -> bool
  - apply(cart, context) -> list[AppliedDiscount]
- At start implement ONE promotion type:
  - PercentOffProductPromotion:
    - applies to a set of SKUs
    - applies a percentage off each matching line (amount = unit_price * qty * pct)
- Define rounding rules now:
  - Money quantizes to 2 decimals using ROUND_HALF_UP
  - Line-level discount is rounded per line, then summed.

Deliverables:
- Package structure: promo_engine/
  - domain.py (types)
  - promotions.py (promotion classes)
  - engine.py (PromotionEngine)
  - tests/ with unittest tests
- Provide 3-5 initial tests:
  - subtotal without promos
  - percent off single line
  - percent off multiple lines with only some eligible
  - explainability contains promotion id and readable details
- Keep the code readable and intentionally “unfinished” for later steps (e.g., leave TODOs or extension points).
```



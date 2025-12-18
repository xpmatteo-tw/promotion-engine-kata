# Promotion engine kata

AIFSD training exercise.


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

# Getting started

This is the prompt you can use to get started.  After code generation, **make sure to review the result and ask for improvements**.

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

# Next steps

The following are incremental steps that you can use to practice TDD with AI agents.  Experiment with asking for all the tests, and then implementing one test at a time versus implementing all at once.

------------------------------------------------------------------------

## Step 1 --- Percent Discount on Products

**Goal:** Implement the baseline pricing engine with a simple percentage
discount on selected products.

### Business Rules

-   Discount applies per eligible line.
-   Discount amount = unit_price × quantity × percentage.
-   Rounding happens per line.

### Acceptance Examples

-   2 × SKU_A at €10.00, 1 × SKU_B at €5.00\
    → Subtotal: €25.00\
    → 10% off SKU_A = €2.00\
    → Total: €23.00

-   Explainability includes:

    -   promotion id
    -   percentage
    -   affected SKUs

------------------------------------------------------------------------

## Step 2 --- Date and Time Applicability

**Goal:** Promotions are only valid during a configured time window.

### Business Rules

-   Promotion has optional:
    -   valid_from (datetime)
    -   valid_to (datetime)
-   Applicability depends on PricingContext.now.

### Acceptance Examples

-   Promo valid from Jan 1 to Jan 31:
    -   Jan 10 → applies
    -   Feb 1 → does not apply
-   Boundary conditions are explicitly tested.

------------------------------------------------------------------------

## Step 3 --- Quantity-Based Promotions (X for Y)

**Goal:** Introduce "buy X, pay Y" promotions (e.g. 3×2).

### Business Rules

-   Applies to a single SKU.
-   For every group of X items, (X − Y) items are free.
-   Discount = unit_price × (X − Y) × number_of_groups.

### Acceptance Examples

-   3 × SKU_A at €10.00 → discount €10.00
-   4 × SKU_A at €10.00 → still discount €10.00
-   6 × SKU_A at €10.00 → discount €20.00

------------------------------------------------------------------------

## Step 4 --- Combining Promotions and Conflict Resolution

**Goal:** Define how multiple promotions interact.

### Introduce

-   StackingPolicy:
    -   STACK
    -   EXCLUSIVE_BEST_FOR_CUSTOMER
    -   EXCLUSIVE_PRIORITY

### Business Rules

-   Engine evaluates all applicable promotions.
-   Policy decides which discounts are applied.

### Acceptance Examples

-   Cart: 3 × SKU_A at €10.00
    -   10% off → €3.00
    -   3×2 → €10.00
-   Best-for-customer → only 3×2 applies
-   Stack → both apply

------------------------------------------------------------------------

## Step 5 --- Customer Segmentation

**Goal:** Promotions can target specific customer segments.

### Business Rules

-   Promotion defines required customer tags.
-   PricingContext.customer_tags must satisfy them.

### Acceptance Examples

-   Promo for "gold" customers:
    -   Tags include "gold" → applies
    -   Tags exclude "gold" → does not apply

------------------------------------------------------------------------

## Step 6 --- Explainability and Decisions

**Goal:** Make promotion evaluation transparent.

### Introduce

-   PromotionDecision / EvaluationTrace:
    -   applicable: bool
    -   reason: str
    -   computed_discount: Money (optional)

### Acceptance Examples

-   Promotion evaluated as:
    -   "Skipped: outside validity window"
    -   "Skipped: customer not eligible"
    -   "Applied: 10% off SKU_A"

------------------------------------------------------------------------

## Optional Extension Steps

-   Order-level discounts with allocation
-   Discount caps (per promo / per cart)
-   Rounding strategy variations
-   Returns and repricing
-   Promotion budgets

------------------------------------------------------------------------

## Teaching Notes

This kata is designed to: - Encourage small test increments - Expose
domain ambiguity - Highlight limitations of naïve AI-generated
solutions - Create discussion around design trade-offs

Each step should **break existing assumptions** while keeping earlier
tests valid.

# Promotion engine kata

AIFSD training exercise.  You may use any AI coding assistant, e.g.: Claude Code, Gemini CLI, Cursor, Windsurf, ... any tool that has an "agent" mode will do.

Instructions:

1. Clone this repository
2. `cd` to your preferred language version (or the least-disliked language version)
3. Start your favorite AI coding assistant and ask it to analyze the codebase.  For Claude Code, Gemini CLI, OpenAI Codex, you can use the `/init` command.
4. Commit the output of the codebase analysis
5. Read Step 1, modify it at will, and feed it to a coding agent.  You may want to do any or all of these steps
   - ask it to analyze what's missing, and then create an implementation plan
   - ask it to list the tests that will be needed (just the titles)
   - ask it to write the tests only
   - ask it to write the production code
   - ask it to criticize the production code

Experiment with variations, observe what results you get.  And have fun!

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

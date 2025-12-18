# Promotion Engine Kata (Go)

A Test-Driven Development (TDD) exercise for building a flexible promotion engine system.

## Overview

This project implements a promotion pricing engine that can apply various promotions to shopping carts. It's designed with clean domain modeling, layered architecture, type safety, and testability in mind.

## Project Structure

The code is organized into four layers following Go best practices:

```
go/
├── domain/          # Core business value types and domain models
├── pricing/         # Pricing-specific types and results
├── promotions/      # Promotion definitions and implementations
└── engine/          # Orchestration and pricing engine
```

### domain/ - Core Business Types

Contains fundamental value types and domain models that form the foundation of the system.

**Value Types (Domain Primitives):**
- **Money** - Represents monetary amounts in cents with proper rounding (ROUND_HALF_UP)
  - Constructors: `Euros(float64)`, `CentsAmount(int64)`
  - Operations: `Add()`, `Subtract()`, `Multiply()`, comparisons
  - Quantized to 2 decimals with epsilon handling for floating-point precision

- **Percentage** - Represents percentages between 0-100
  - Constructor: `NewPercentage(float64)` with validation
  - Method: `AsDecimal()` for converting to decimal form

- **Quantity** - Represents non-negative quantities
  - Constructor: `NewQuantity(int)` with validation
  - Operations: `Add()`, `Subtract()` with validation

- **Sku** - Stock Keeping Unit identifier wrapper
  - Constructor: `NewSku(string)` with validation

- **PromotionID** - Promotion identifier wrapper
  - Constructor: `NewPromotionID(string)` with validation

**Domain Models:**
- **Product** - Represents a product with SKU, name, and category
- **LineItem** - Represents a cart line item with Product, Quantity, UnitPrice
  - Method: `Total()` calculates line total
- **Cart** - Shopping cart containing line items
  - Methods: `AddLine()`, `Subtotal()`

### pricing/ - Pricing Types

Contains types related to pricing results and context.

- **PricingContext** - Context for pricing decisions
  - Contains: Now (timestamp), Channel, CustomerID, CustomerTags
  - Method: `HasTag(string)` for checking customer tags

- **AppliedDiscount** - Records applied discounts
  - Preserves explainability with promotion ID, amount, target, details
  - Optional per-SKU allocations for transparency
  - Target: "line" or "cart"

- **PriceSummary** - Final pricing result
  - Contains: Subtotal, DiscountTotal, Total, AppliedDiscounts

### promotions/ - Promotion System

Contains the promotion interface and implementations.

- **Promotion** (interface) - Defines the contract for promotions
  - `ID() PromotionID`
  - `IsApplicable(cart, context) bool`
  - `Apply(cart, context) []AppliedDiscount`

- **StubPromotion** - Test stub for promotions
  - Configurable behavior for testing

### engine/ - Orchestration Layer

Contains the main promotion engine that coordinates everything.

- **PromotionEngine** - Orchestrates promotion application
  - Method: `Price(cart, context) PriceSummary`
  - Evaluates all promotions and calculates final price

## Running Tests

```bash
# Run all tests
go test -v ./...

# Run tests for a specific package
go test -v ./domain
go test -v ./pricing
go test -v ./promotions
go test -v ./engine

# Run with coverage
go test -v -cover ./...

# Run acceptance tests only
go test -v ./engine -run Acceptance
```

## Package Dependencies

The layered architecture establishes clear dependency rules:

```
engine → promotions → pricing → domain
                  ↓              ↓
                  └──────────────┘
```

- **domain/** - No dependencies (foundational layer)
- **pricing/** - Depends on `domain/`
- **promotions/** - Depends on `domain/` and `pricing/`
- **engine/** - Depends on all layers

This ensures:
- Clear separation of concerns
- No circular dependencies
- Easy to test each layer independently
- Foundation types are widely accessible

## Rounding Rules

- Money uses ROUND_HALF_UP to 2 decimal places
- A small epsilon (1e-9) is added to handle floating-point precision issues
- Line-level discounts are rounded per line, then summed

## Design Decisions

1. **Layered Architecture**: Organizes code by responsibility (domain, pricing, promotions, engine)

2. **Type Safety**: Custom value types prevent mixing incompatible values (e.g., Money with Quantity)

3. **Immutability**: Value types return new instances rather than mutating

4. **Validation**: Constructors validate inputs and return errors or panic with Must* variants

5. **Explainability**: AppliedDiscount tracks promotion ID, details, and optional per-SKU allocations

6. **Extensibility**: Promotion interface allows easy addition of new promotion types

7. **Package Organization**: Each layer has a focused responsibility with clear dependencies

## Next Steps (TODOs)

The codebase is intentionally "unfinished" to allow for:

1. **Implement specific promotion types** in `promotions/`:
   - PercentOffCart (e.g., 10% off entire cart)
   - PercentOffSku (e.g., 20% off specific products)
   - BuyXGetY (e.g., Buy 2, Get 1 Free)
   - FixedAmountOff (e.g., €5 off)
   - MinimumPurchasePromotion (e.g., €10 off when spending €50+)

2. **Add time-based promotions**:
   - ValidFrom/ValidUntil date ranges
   - Day-of-week restrictions

3. **Add channel-based promotions**:
   - Online-only or in-store-only promotions

4. **Add customer-segment promotions**:
   - New customer discounts
   - VIP/Gold member discounts

5. **Add combination rules**:
   - Stackable vs. exclusive promotions
   - Priority/precedence handling

6. **Add more sophisticated allocation**:
   - Prorate discounts across multiple SKUs
   - Handle line-level vs. cart-level discounts

## Testing Philosophy

This project follows ATDD (Acceptance Test-Driven Development):

1. Start with acceptance tests defining user-facing behavior
2. Work backward to implement necessary components
3. Write unit tests for individual components using TDD
4. Refactor while keeping tests green

See `engine/acceptance_test.go` for end-to-end examples.

## Benefits of This Structure

**Compared to a flat package:**
- ✅ Clear architectural boundaries
- ✅ Easier to navigate by responsibility
- ✅ Prevents unintended coupling
- ✅ Each package can be understood independently
- ✅ Easy to see dependencies at a glance
- ✅ Natural place for new code (clear which layer)

**Trade-offs:**
- More import statements needed
- Slightly more ceremony for small projects
- But: scales better as project grows

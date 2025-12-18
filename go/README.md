# Promotion Engine Kata (Go)

A Test-Driven Development (TDD) exercise for building a flexible promotion engine system.

## Overview

This project implements a promotion pricing engine that can apply various promotions to shopping carts. It's designed with clean domain modeling, type safety, and testability in mind.

## Project Structure

### Value Types (Domain Primitives)

- **Money** (`money.go`) - Represents monetary amounts in cents with proper rounding (ROUND_HALF_UP)
  - Constructors: `Euros(float64)`, `CentsAmount(int64)`
  - Operations: `Add()`, `Subtract()`, `Multiply()`, comparisons
  - Quantized to 2 decimals with epsilon handling for floating-point precision

- **Percentage** (`percentage.go`) - Represents percentages between 0-100
  - Constructor: `NewPercentage(float64)` with validation
  - Method: `AsDecimal()` for converting to decimal form

- **Quantity** (`quantity.go`) - Represents non-negative quantities
  - Constructor: `NewQuantity(int)` with validation
  - Operations: `Add()`, `Subtract()` with validation

- **Sku** (`sku.go`) - Stock Keeping Unit identifier wrapper
  - Constructor: `NewSku(string)` with validation

- **PromotionID** (`promotionid.go`) - Promotion identifier wrapper
  - Constructor: `NewPromotionID(string)` with validation

### Domain Model

- **Product** (`product.go`) - Represents a product with SKU, name, and category

- **LineItem** (`lineitem.go`) - Represents a cart line item
  - Contains: Product, Quantity, UnitPrice
  - Method: `Total()` calculates line total

- **Cart** (`cart.go`) - Shopping cart containing line items
  - Methods: `AddLine()`, `Subtotal()`

- **AppliedDiscount** (`applieddiscount.go`) - Records applied discounts
  - Preserves explainability with promotion ID, amount, target, details, and optional per-SKU allocations
  - Target: "line" or "cart"

- **PriceSummary** (`pricesummary.go`) - Final pricing result
  - Contains: Subtotal, DiscountTotal, Total, AppliedDiscounts

- **PricingContext** (`pricingcontext.go`) - Context for pricing decisions
  - Contains: Now (timestamp), Channel, CustomerID, CustomerTags
  - Method: `HasTag(string)` for checking customer tags

### Engine

- **Promotion** (`promotion.go`) - Interface for promotions
  - `ID() PromotionID`
  - `IsApplicable(cart, context) bool`
  - `Apply(cart, context) []AppliedDiscount`

- **PromotionEngine** (`promotionengine.go`) - Orchestrates promotion application
  - Method: `Price(cart, context) PriceSummary`
  - Evaluates all promotions and calculates final price

- **StubPromotion** (`stubpromotion.go`) - Test stub for promotions
  - Configurable behavior for testing

## Running Tests

```bash
# Run all tests
go test -v ./...

# Run specific test
go test -v -run TestPromotionEngine

# Run with coverage
go test -v -cover ./...
```

## Rounding Rules

- Money uses ROUND_HALF_UP to 2 decimal places
- A small epsilon (1e-9) is added to handle floating-point precision issues
- Line-level discounts are rounded per line, then summed

## Design Decisions

1. **Type Safety**: Custom value types prevent mixing incompatible values (e.g., Money with Quantity)

2. **Immutability**: Value types return new instances rather than mutating

3. **Validation**: Constructors validate inputs and return errors or panic with Must* variants

4. **Explainability**: AppliedDiscount tracks promotion ID, details, and optional per-SKU allocations

5. **Extensibility**: Promotion interface allows easy addition of new promotion types

## Next Steps (TODOs)

The codebase is intentionally "unfinished" to allow for:

1. **Implement specific promotion types**:
   - PercentOffCart (e.g., 10% off entire cart)
   - PercentOffSku (e.g., 20% off specific products)
   - BuyXGetY (e.g., Buy 2, Get 1 Free)
   - FixedAmountOff (e.g., $5 off)
   - MinimumPurchasePromotion (e.g., $10 off when spending $50+)

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

See `acceptance_test.go` for end-to-end examples.

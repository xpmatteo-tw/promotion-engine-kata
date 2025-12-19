# Promotion Engine - TypeScript Implementation

TypeScript implementation of the Promotion Engine Kata following ATDD principles.

## Overview

A pricing engine that applies promotions to shopping carts with full explainability. Built with:

- **TypeScript 5.x** with strict mode for type safety
- **Vitest** for fast, modern testing
- **decimal.js** for precise monetary calculations
- **ESLint + Prettier** for code quality

## Project Structure

```
src/
├── domain/              # Core value types and domain models
│   ├── Money.ts         # Monetary values with ROUND_HALF_UP
│   ├── Percentage.ts    # Percentage (0-100)
│   ├── Quantity.ts      # Non-negative quantities
│   ├── Sku.ts          # Stock keeping unit ID
│   ├── PromotionId.ts  # Promotion identifier
│   ├── Product.ts      # Product catalog entry
│   ├── LineItem.ts     # Cart line item
│   └── Cart.ts         # Shopping cart
├── pricing/            # Pricing calculation types
│   ├── PricingContext.ts    # Context for pricing
│   ├── AppliedDiscount.ts   # Discount record
│   └── PriceSummary.ts      # Pricing breakdown
├── promotions/         # Promotion system
│   ├── Promotion.ts    # Promotion interface
│   └── StubPromotion.ts     # Test helper
└── engine/            # Core pricing engine
    ├── PromotionEngine.ts   # Main orchestrator
    └── acceptance.test.ts   # ATDD tests
```

## Getting Started

### Install Dependencies

```bash
npm install
```

### Build

```bash
npm run build
```

### Run Tests

```bash
# Run all tests
npm test

# Watch mode
npm run test:watch

# With coverage
npm run test:coverage
```

### Clean Build Artifacts

```bash
npm run clean
```

## Design Principles

### Value Types

All domain primitives are wrapped in dedicated classes:

- **Money**: Immutable, quantized to 2 decimals, ROUND_HALF_UP rounding
- **Percentage**: Range-validated (0-100)
- **Quantity**: Non-negative integers
- **Sku & PromotionId**: Non-empty strings

### Immutability

- All value types and domain models are immutable
- Methods return new instances instead of mutating
- Collections use `readonly` and `Object.freeze()`

### Type Safety

- Strict TypeScript mode enabled
- No `any` types
- Explicit types throughout
- Compile-time guarantees

### Precision

- Uses `decimal.js` for all monetary calculations
- Consistent ROUND_HALF_UP rounding strategy
- Avoids floating-point errors

### Explainability

- `AppliedDiscount` captures promotion ID, amount, target, details
- Optional per-SKU allocations for transparency
- Complete audit trail in `PriceSummary`

## Testing Strategy

### ATDD (Acceptance Test-Driven Development)

1. **Acceptance tests** define complete user-facing behavior (see `acceptance.test.ts`)
2. **Unit tests** verify individual component behavior
3. **Stub promotions** enable testing without real promotion implementations

### Test Coverage

Run with coverage to see detailed metrics:

```bash
npm run test:coverage
```

Target: > 90% coverage across all modules

## Key Features

### PromotionEngine

The core orchestrator that:

1. Calculates cart subtotal
2. Filters applicable promotions
3. Collects all discounts
4. Sums discount amounts
5. Returns comprehensive pricing summary

### Promotion Interface

```typescript
interface Promotion {
  id(): PromotionId;
  isApplicable(cart: Cart, context: PricingContext): boolean;
  apply(cart: Cart, context: PricingContext): AppliedDiscount[];
}
```

### PricingContext

Provides context for promotion evaluation:

- **now**: Current timestamp
- **channel**: Sales channel (e.g., "online")
- **customerId**: Customer identifier
- **customerTags**: Customer segments (e.g., {"new", "gold"})

## Future Extensions

See TODO comments in source code for planned features:

- Promotion priority/ordering
- Combination rules (STACK, EXCLUSIVE_BEST, EXCLUSIVE_PRIORITY)
- Time-based applicability
- Customer segment targeting
- Specific promotion types (PercentOff, BuyXGetY, etc.)

## License

MIT

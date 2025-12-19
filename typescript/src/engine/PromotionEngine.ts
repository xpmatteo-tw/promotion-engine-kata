// ABOUTME: Orchestrates promotion evaluation and pricing calculation
// ABOUTME: Applies applicable promotions and generates comprehensive pricing summaries

import { Cart, Money } from '../domain';
import { PricingContext, PriceSummary } from '../pricing';
import { Promotion } from '../promotions';

// TODO: Add promotion priority/ordering support
// TODO: Add promotion combination rules (STACK, EXCLUSIVE_BEST, EXCLUSIVE_PRIORITY)
// TODO: Consider tracking which promotions were evaluated but not applicable

export class PromotionEngine {
  private readonly promotions: readonly Promotion[];

  constructor(promotions: Promotion[]) {
    // Defensive copy and freeze
    this.promotions = Object.freeze([...promotions]);
  }

  price(cart: Cart, context: PricingContext): PriceSummary {
    // Algorithm:
    // 1. Calculate cart subtotal
    const subtotal = cart.subtotal();

    // 2. Filter applicable promotions and collect all discounts
    const appliedDiscounts = this.promotions
      .filter((promo) => promo.isApplicable(cart, context))
      .flatMap((promo) => promo.apply(cart, context));

    // 3. Calculate total discount
    const discountTotal =
      appliedDiscounts.length === 0
        ? Money.euros(0)
        : appliedDiscounts.map((d) => d.amount).reduce((acc, curr) => acc.add(curr));

    // 4. Calculate final total
    const total = subtotal.subtract(discountTotal);

    // 5. Return complete summary
    return new PriceSummary(subtotal, discountTotal, total, appliedDiscounts);
  }
}

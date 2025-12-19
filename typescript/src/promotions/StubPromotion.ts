// ABOUTME: Test helper that implements Promotion with configurable behavior
// ABOUTME: Used for testing the promotion engine without real promotion logic

import { Promotion } from './Promotion';
import { PromotionId } from '../domain/PromotionId';
import { Cart } from '../domain/Cart';
import { PricingContext, AppliedDiscount } from '../pricing';

export class StubPromotion implements Promotion {
  constructor(
    private readonly promotionId: PromotionId,
    private readonly shouldApply: boolean,
    private readonly discounts: AppliedDiscount[]
  ) {}

  id(): PromotionId {
    return this.promotionId;
  }

  isApplicable(_cart: Cart, _context: PricingContext): boolean {
    return this.shouldApply;
  }

  apply(_cart: Cart, _context: PricingContext): AppliedDiscount[] {
    return this.discounts;
  }
}

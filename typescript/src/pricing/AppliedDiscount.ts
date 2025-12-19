// ABOUTME: Record of a discount that was applied to a cart
// ABOUTME: Includes promotion ID, amount, target scope, details, and optional allocations

import { PromotionId } from '../domain/PromotionId';
import { Money } from '../domain/Money';
import { Sku } from '../domain/Sku';

export class AppliedDiscount {
  constructor(
    public readonly promotionId: PromotionId,
    public readonly amount: Money,
    public readonly target: 'line' | 'cart',
    public readonly details: string,
    public readonly allocations?: ReadonlyMap<Sku, Money>
  ) {}
}

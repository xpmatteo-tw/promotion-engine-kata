// ABOUTME: Complete pricing breakdown for a cart
// ABOUTME: Contains subtotal, discount total, final total, and applied discounts

import { Money } from '../domain/Money';
import { AppliedDiscount } from './AppliedDiscount';

export class PriceSummary {
  private readonly _appliedDiscounts: readonly AppliedDiscount[];

  constructor(
    public readonly subtotal: Money,
    public readonly discountTotal: Money,
    public readonly total: Money,
    appliedDiscounts: AppliedDiscount[]
  ) {
    // Defensive copy and freeze
    this._appliedDiscounts = Object.freeze([...appliedDiscounts]);
  }

  get appliedDiscounts(): readonly AppliedDiscount[] {
    return this._appliedDiscounts;
  }
}

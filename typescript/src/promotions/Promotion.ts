// ABOUTME: Interface defining the contract for promotion implementations
// ABOUTME: Promotions evaluate cart eligibility and generate discount applications

import { Cart } from '../domain/Cart';
import { PromotionId } from '../domain/PromotionId';
import { PricingContext, AppliedDiscount } from '../pricing';

// TODO: Add time-based applicability (validFrom, validUntil)
// TODO: Add day-of-week and time-of-day restrictions
// TODO: Add customer segment targeting using context.customerTags
// TODO: Implement specific promotion types:
//   - PercentOffProduct: Apply percentage discount to specific products
//   - BuyXGetY: Buy X items, get Y items free/discounted
//   - FixedAmountOff: Fixed dollar amount off
//   - ThresholdPromotion: Minimum purchase amount required

export interface Promotion {
  id(): PromotionId;

  isApplicable(cart: Cart, context: PricingContext): boolean;

  apply(cart: Cart, context: PricingContext): AppliedDiscount[];
}

import { describe, it, expect } from 'vitest';
import { AppliedDiscount } from './AppliedDiscount';
import { PromotionId } from '../domain/PromotionId';
import { Money } from '../domain/Money';
import { Sku } from '../domain/Sku';

describe('AppliedDiscount', () => {
  describe('construction', () => {
    it('should construct without allocations', () => {
      const promotionId = new PromotionId('PROMO-123');
      const amount = Money.euros('10.00');
      const discount = new AppliedDiscount(promotionId, amount, 'cart', 'Test discount');

      expect(discount.promotionId).toBe(promotionId);
      expect(discount.amount).toBe(amount);
      expect(discount.target).toBe('cart');
      expect(discount.details).toBe('Test discount');
      expect(discount.allocations).toBeUndefined();
    });

    it('should construct with allocations', () => {
      const promotionId = new PromotionId('PROMO-123');
      const amount = Money.euros('10.00');
      const skuA = new Sku('SKU-A');
      const skuB = new Sku('SKU-B');
      const allocations = new Map<Sku, Money>([
        [skuA, Money.euros('6.00')],
        [skuB, Money.euros('4.00')],
      ]);

      const discount = new AppliedDiscount(
        promotionId,
        amount,
        'line',
        'Test discount with allocations',
        allocations
      );

      expect(discount.promotionId).toBe(promotionId);
      expect(discount.amount).toBe(amount);
      expect(discount.target).toBe('line');
      expect(discount.details).toBe('Test discount with allocations');
      expect(discount.allocations).toBe(allocations);
      expect(discount.allocations?.size).toBe(2);
    });
  });
});

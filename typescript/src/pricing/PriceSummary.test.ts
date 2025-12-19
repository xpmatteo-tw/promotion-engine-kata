import { describe, it, expect } from 'vitest';
import { PriceSummary } from './PriceSummary';
import { AppliedDiscount } from './AppliedDiscount';
import { Money } from '../domain/Money';
import { PromotionId } from '../domain/PromotionId';

describe('PriceSummary', () => {
  describe('construction', () => {
    it('should construct with no applied discounts', () => {
      const subtotal = Money.euros('45.00');
      const discountTotal = Money.euros('0.00');
      const total = Money.euros('45.00');
      const summary = new PriceSummary(subtotal, discountTotal, total, []);

      expect(summary.subtotal).toBe(subtotal);
      expect(summary.discountTotal).toBe(discountTotal);
      expect(summary.total).toBe(total);
      expect(summary.appliedDiscounts).toHaveLength(0);
    });

    it('should construct with applied discounts', () => {
      const subtotal = Money.euros('45.00');
      const discountTotal = Money.euros('5.00');
      const total = Money.euros('40.00');
      const discount = new AppliedDiscount(
        new PromotionId('PROMO-123'),
        Money.euros('5.00'),
        'cart',
        'Test discount'
      );
      const summary = new PriceSummary(subtotal, discountTotal, total, [discount]);

      expect(summary.subtotal).toBe(subtotal);
      expect(summary.discountTotal).toBe(discountTotal);
      expect(summary.total).toBe(total);
      expect(summary.appliedDiscounts).toHaveLength(1);
      expect(summary.appliedDiscounts[0]).toBe(discount);
    });
  });

  describe('immutability', () => {
    it('should not allow external modification of applied discounts', () => {
      const discount = new AppliedDiscount(
        new PromotionId('PROMO-123'),
        Money.euros('5.00'),
        'cart',
        'Test discount'
      );
      const summary = new PriceSummary(
        Money.euros('45.00'),
        Money.euros('5.00'),
        Money.euros('40.00'),
        [discount]
      );

      // TypeScript should prevent this at compile time,
      // but we test runtime immutability
      expect(() => {
        // @ts-expect-error - Testing runtime immutability
        summary.appliedDiscounts.push(discount);
      }).toThrow();
    });
  });
});

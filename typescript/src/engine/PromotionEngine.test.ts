import { describe, it, expect } from 'vitest';
import { PromotionEngine } from './PromotionEngine';
import { StubPromotion } from '../promotions/StubPromotion';
import { Cart, LineItem, Product, Sku, Quantity, Money, PromotionId } from '../domain';
import { PricingContext, AppliedDiscount } from '../pricing';

describe('PromotionEngine', () => {
  describe('construction', () => {
    it('should construct with empty promotions list', () => {
      const engine = new PromotionEngine([]);
      expect(engine).toBeDefined();
    });

    it('should construct with promotions', () => {
      const promotion = new StubPromotion(new PromotionId('PROMO-1'), true, []);
      const engine = new PromotionEngine([promotion]);
      expect(engine).toBeDefined();
    });
  });

  describe('pricing', () => {
    it('should return subtotal when no promotions applicable', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const cart = new Cart([new LineItem(product, new Quantity(1), Money.euros('10.00'))]);
      const context = new PricingContext(
        new Date(),
        'online',
        'CUST123',
        new Set(['new'])
      );

      const promotion = new StubPromotion(new PromotionId('PROMO-1'), false, []);
      const engine = new PromotionEngine([promotion]);
      const summary = engine.price(cart, context);

      expect(summary.subtotal.amount.toFixed(2)).toBe('10.00');
      expect(summary.discountTotal.amount.toFixed(2)).toBe('0.00');
      expect(summary.total.amount.toFixed(2)).toBe('10.00');
      expect(summary.appliedDiscounts).toHaveLength(0);
    });

    it('should apply single promotion discount', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const cart = new Cart([new LineItem(product, new Quantity(1), Money.euros('10.00'))]);
      const context = new PricingContext(
        new Date(),
        'online',
        'CUST123',
        new Set(['new'])
      );

      const discount = new AppliedDiscount(
        new PromotionId('PROMO-1'),
        Money.euros('2.00'),
        'cart',
        'Test discount'
      );
      const promotion = new StubPromotion(new PromotionId('PROMO-1'), true, [discount]);
      const engine = new PromotionEngine([promotion]);
      const summary = engine.price(cart, context);

      expect(summary.subtotal.amount.toFixed(2)).toBe('10.00');
      expect(summary.discountTotal.amount.toFixed(2)).toBe('2.00');
      expect(summary.total.amount.toFixed(2)).toBe('8.00');
      expect(summary.appliedDiscounts).toHaveLength(1);
    });

    it('should sum multiple promotion discounts', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const cart = new Cart([new LineItem(product, new Quantity(1), Money.euros('20.00'))]);
      const context = new PricingContext(
        new Date(),
        'online',
        'CUST123',
        new Set(['new'])
      );

      const discount1 = new AppliedDiscount(
        new PromotionId('PROMO-1'),
        Money.euros('3.00'),
        'cart',
        'Discount 1'
      );
      const discount2 = new AppliedDiscount(
        new PromotionId('PROMO-2'),
        Money.euros('2.00'),
        'cart',
        'Discount 2'
      );

      const promotion1 = new StubPromotion(new PromotionId('PROMO-1'), true, [discount1]);
      const promotion2 = new StubPromotion(new PromotionId('PROMO-2'), true, [discount2]);

      const engine = new PromotionEngine([promotion1, promotion2]);
      const summary = engine.price(cart, context);

      expect(summary.subtotal.amount.toFixed(2)).toBe('20.00');
      expect(summary.discountTotal.amount.toFixed(2)).toBe('5.00');
      expect(summary.total.amount.toFixed(2)).toBe('15.00');
      expect(summary.appliedDiscounts).toHaveLength(2);
    });
  });
});

// ABOUTME: Acceptance tests for the promotion engine pricing system
// ABOUTME: Defines complete user-facing behavior for cart pricing with promotions

import { describe, it, expect, beforeEach } from 'vitest';
import { PromotionEngine } from './PromotionEngine';
import { StubPromotion } from '../promotions/StubPromotion';
import {
  Cart,
  LineItem,
  Product,
  Sku,
  Quantity,
  Money,
  PromotionId,
} from '../domain';
import { PricingContext, AppliedDiscount } from '../pricing';

describe('Promotion Engine Acceptance Tests', () => {
  let context: PricingContext;
  let productA: Product;
  let productB: Product;
  let productC: Product;

  beforeEach(() => {
    // Shared context for all tests
    context = new PricingContext(
      new Date('2025-12-18T10:00:00Z'),
      'online',
      'CUST001',
      new Set(['new'])
    );

    // Common products
    productA = new Product(new Sku('SKU-A'), 'Product A', 'electronics');
    productB = new Product(new Sku('SKU-B'), 'Product B', 'electronics');
    productC = new Product(new Sku('SKU-C'), 'Product C', 'books');
  });

  it('should calculate subtotal and total when no promotions are configured', () => {
    const cart = new Cart([
      new LineItem(productA, new Quantity(2), Money.euros('10.00')),
      new LineItem(productB, new Quantity(1), Money.euros('25.00')),
    ]);

    const engine = new PromotionEngine([]);
    const summary = engine.price(cart, context);

    expect(summary.subtotal.amount.toFixed(2)).toBe('45.00');
    expect(summary.discountTotal.amount.toFixed(2)).toBe('0.00');
    expect(summary.total.amount.toFixed(2)).toBe('45.00');
    expect(summary.appliedDiscounts).toHaveLength(0);
  });

  it('should apply a single applicable promotion to a cart', () => {
    const cart = new Cart([
      new LineItem(productA, new Quantity(2), Money.euros('10.00')),
    ]);

    const discount = new AppliedDiscount(
      new PromotionId('PROMO-10-OFF-A'),
      Money.euros('2.00'),
      'line',
      '$2 off SKU-A',
      new Map([[new Sku('SKU-A'), Money.euros('2.00')]])
    );

    const promotion = new StubPromotion(
      new PromotionId('PROMO-10-OFF-A'),
      true,
      [discount]
    );

    const engine = new PromotionEngine([promotion]);
    const summary = engine.price(cart, context);

    // 2 * $10.00 = $20.00, $2 off = $18.00
    expect(summary.subtotal.amount.toFixed(2)).toBe('20.00');
    expect(summary.discountTotal.amount.toFixed(2)).toBe('2.00');
    expect(summary.total.amount.toFixed(2)).toBe('18.00');
    expect(summary.appliedDiscounts).toHaveLength(1);

    const appliedDiscount = summary.appliedDiscounts[0];
    expect(appliedDiscount.promotionId.equals(new PromotionId('PROMO-10-OFF-A'))).toBe(
      true
    );
    expect(appliedDiscount.amount.amount.toFixed(2)).toBe('2.00');
    expect(appliedDiscount.target).toBe('line');
  });

  it('should apply promotion with allocations across multiple line items', () => {
    const cart = new Cart([
      new LineItem(productA, new Quantity(1), Money.euros('10.00')),
      new LineItem(productB, new Quantity(1), Money.euros('20.00')),
      new LineItem(productC, new Quantity(1), Money.euros('15.00')),
    ]);

    // Promotion with allocations to SKU-A and SKU-B
    const discount = new AppliedDiscount(
      new PromotionId('PROMO-15-OFF-ELECTRONICS'),
      Money.euros('4.50'),
      'line',
      '$4.50 off electronics',
      new Map([
        [new Sku('SKU-A'), Money.euros('1.50')],
        [new Sku('SKU-B'), Money.euros('3.00')],
      ])
    );

    const promotion = new StubPromotion(
      new PromotionId('PROMO-15-OFF-ELECTRONICS'),
      true,
      [discount]
    );

    const engine = new PromotionEngine([promotion]);
    const summary = engine.price(cart, context);

    // Subtotal: $10 + $20 + $15 = $45
    // Discount: $1.50 + $3.00 = $4.50
    // SKU-C is not in allocations
    expect(summary.subtotal.amount.toFixed(2)).toBe('45.00');
    expect(summary.discountTotal.amount.toFixed(2)).toBe('4.50');
    expect(summary.total.amount.toFixed(2)).toBe('40.50');
    expect(summary.appliedDiscounts).toHaveLength(1);

    const appliedDiscount = summary.appliedDiscounts[0];
    expect(appliedDiscount.allocations).toBeDefined();
    expect(appliedDiscount.allocations?.size).toBe(2);

    // Find SKU-A allocation
    const skuAAllocation = Array.from(appliedDiscount.allocations?.entries() || []).find(
      ([sku]) => sku.value === 'SKU-A'
    );
    expect(skuAAllocation?.[1].amount.toFixed(2)).toBe('1.50');

    // Find SKU-B allocation
    const skuBAllocation = Array.from(appliedDiscount.allocations?.entries() || []).find(
      ([sku]) => sku.value === 'SKU-B'
    );
    expect(skuBAllocation?.[1].amount.toFixed(2)).toBe('3.00');
  });

  it('should verify that applied discounts contain promotion ID and readable details', () => {
    const cart = new Cart([
      new LineItem(productA, new Quantity(1), Money.euros('50.00')),
    ]);

    const discount = new AppliedDiscount(
      new PromotionId('SUMMER-SALE-2025'),
      Money.euros('10.00'),
      'line',
      'Summer sale: $10 off SKU-A',
      new Map([[new Sku('SKU-A'), Money.euros('10.00')]])
    );

    const promotion = new StubPromotion(
      new PromotionId('SUMMER-SALE-2025'),
      true,
      [discount]
    );

    const engine = new PromotionEngine([promotion]);
    const summary = engine.price(cart, context);

    expect(summary.appliedDiscounts).toHaveLength(1);
    const appliedDiscount = summary.appliedDiscounts[0];

    // Verify explainability
    expect(appliedDiscount.promotionId.equals(new PromotionId('SUMMER-SALE-2025'))).toBe(
      true
    );
    expect(appliedDiscount.details).toBeDefined();
    expect(appliedDiscount.details).toContain('SKU-A');
    expect(appliedDiscount.allocations).toBeDefined();

    // Find SKU-A allocation
    const skuAAllocation = Array.from(appliedDiscount.allocations?.entries() || []).find(
      ([sku]) => sku.value === 'SKU-A'
    );
    expect(skuAAllocation?.[1].amount.toFixed(2)).toBe('10.00');
  });

  it('should verify that multiple promotions can be applied and their discounts are summed', () => {
    const cart = new Cart([
      new LineItem(productA, new Quantity(1), Money.euros('10.00')),
      new LineItem(productB, new Quantity(1), Money.euros('20.00')),
      new LineItem(productC, new Quantity(1), Money.euros('15.00')),
    ]);

    // First promotion gives $2.50 off
    const discount1 = new AppliedDiscount(
      new PromotionId('PROMO-1'),
      Money.euros('2.50'),
      'line',
      'Promotion 1',
      new Map([[new Sku('SKU-A'), Money.euros('2.50')]])
    );

    // Second promotion gives $3.25 off
    const discount2 = new AppliedDiscount(
      new PromotionId('PROMO-2'),
      Money.euros('3.25'),
      'line',
      'Promotion 2',
      new Map([[new Sku('SKU-B'), Money.euros('3.25')]])
    );

    const promotion1 = new StubPromotion(new PromotionId('PROMO-1'), true, [discount1]);

    const promotion2 = new StubPromotion(new PromotionId('PROMO-2'), true, [discount2]);

    const engine = new PromotionEngine([promotion1, promotion2]);
    const summary = engine.price(cart, context);

    // Total discount: $2.50 + $3.25 = $5.75
    expect(summary.subtotal.amount.toFixed(2)).toBe('45.00');
    expect(summary.discountTotal.amount.toFixed(2)).toBe('5.75');
    expect(summary.total.amount.toFixed(2)).toBe('39.25');
    expect(summary.appliedDiscounts).toHaveLength(2);
  });
});

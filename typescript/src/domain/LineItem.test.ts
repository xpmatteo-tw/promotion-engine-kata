import { describe, it, expect } from 'vitest';
import { LineItem } from './LineItem';
import { Product } from './Product';
import { Quantity } from './Quantity';
import { Money } from './Money';
import { Sku } from './Sku';

describe('LineItem', () => {
  describe('construction', () => {
    it('should construct with valid fields', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const quantity = new Quantity(3);
      const unitPrice = Money.euros('10.00');
      const lineItem = new LineItem(product, quantity, unitPrice);

      expect(lineItem.product).toBe(product);
      expect(lineItem.quantity).toBe(quantity);
      expect(lineItem.unitPrice).toBe(unitPrice);
    });
  });

  describe('subtotal', () => {
    it('should calculate subtotal correctly', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const lineItem = new LineItem(product, new Quantity(3), Money.euros('10.00'));

      const subtotal = lineItem.subtotal();
      expect(subtotal.amount.toFixed(2)).toBe('30.00');
    });

    it('should handle rounding in subtotal calculation', () => {
      const product = new Product(new Sku('SKU-B'), 'Product B', 'Books');
      const lineItem = new LineItem(product, new Quantity(3), Money.euros('1.235'));

      const subtotal = lineItem.subtotal();
      // 1.24 (rounded) * 3 = 3.72
      expect(subtotal.amount.toFixed(2)).toBe('3.72');
    });

    it('should return zero for zero quantity', () => {
      const product = new Product(new Sku('SKU-C'), 'Product C', 'Toys');
      const lineItem = new LineItem(product, new Quantity(0), Money.euros('10.00'));

      const subtotal = lineItem.subtotal();
      expect(subtotal.amount.toFixed(2)).toBe('0.00');
    });
  });
});

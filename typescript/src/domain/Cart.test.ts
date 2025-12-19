import { describe, it, expect } from 'vitest';
import { Cart } from './Cart';
import { LineItem } from './LineItem';
import { Product } from './Product';
import { Quantity } from './Quantity';
import { Money } from './Money';
import { Sku } from './Sku';

describe('Cart', () => {
  describe('construction', () => {
    it('should construct with empty lines', () => {
      const cart = new Cart([]);
      expect(cart.lines).toHaveLength(0);
    });

    it('should construct with line items', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const lineItem = new LineItem(product, new Quantity(1), Money.euros('10.00'));
      const cart = new Cart([lineItem]);

      expect(cart.lines).toHaveLength(1);
      expect(cart.lines[0]).toBe(lineItem);
    });
  });

  describe('subtotal', () => {
    it('should return zero for empty cart', () => {
      const cart = new Cart([]);
      const subtotal = cart.subtotal();
      expect(subtotal.amount.toFixed(2)).toBe('0.00');
    });

    it('should calculate subtotal for single line', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const lineItem = new LineItem(product, new Quantity(2), Money.euros('10.00'));
      const cart = new Cart([lineItem]);

      const subtotal = cart.subtotal();
      expect(subtotal.amount.toFixed(2)).toBe('20.00');
    });

    it('should calculate subtotal for multiple lines', () => {
      const productA = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const productB = new Product(new Sku('SKU-B'), 'Product B', 'Books');

      const lineItemA = new LineItem(productA, new Quantity(2), Money.euros('10.00'));
      const lineItemB = new LineItem(productB, new Quantity(1), Money.euros('25.00'));
      const cart = new Cart([lineItemA, lineItemB]);

      const subtotal = cart.subtotal();
      // 2 * 10 + 1 * 25 = 45
      expect(subtotal.amount.toFixed(2)).toBe('45.00');
    });

    it('should handle rounding across multiple lines', () => {
      const productA = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const productB = new Product(new Sku('SKU-B'), 'Product B', 'Books');

      const lineItemA = new LineItem(productA, new Quantity(3), Money.euros('1.235'));
      const lineItemB = new LineItem(productB, new Quantity(2), Money.euros('2.225'));

      const cart = new Cart([lineItemA, lineItemB]);

      const subtotal = cart.subtotal();
      // Line A: 1.24 * 3 = 3.72
      // Line B: 2.23 * 2 = 4.46
      // Total: 3.72 + 4.46 = 8.18
      expect(subtotal.amount.toFixed(2)).toBe('8.18');
    });
  });

  describe('immutability', () => {
    it('should not allow external modification of lines', () => {
      const product = new Product(new Sku('SKU-A'), 'Product A', 'Electronics');
      const lineItem = new LineItem(product, new Quantity(1), Money.euros('10.00'));
      const cart = new Cart([lineItem]);

      // TypeScript should prevent this at compile time,
      // but we test runtime immutability
      expect(() => {
        // @ts-expect-error - Testing runtime immutability
        cart.lines.push(lineItem);
      }).toThrow();
    });
  });
});

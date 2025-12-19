import { describe, it, expect } from 'vitest';
import { Product } from './Product';
import { Sku } from './Sku';

describe('Product', () => {
  describe('construction', () => {
    it('should construct with valid fields', () => {
      const sku = new Sku('SKU-A');
      const product = new Product(sku, 'Product A', 'Electronics');
      expect(product.sku).toBe(sku);
      expect(product.name).toBe('Product A');
      expect(product.category).toBe('Electronics');
    });
  });
});

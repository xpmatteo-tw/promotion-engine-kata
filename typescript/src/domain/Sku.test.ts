import { describe, it, expect } from 'vitest';
import { Sku } from './Sku';

describe('Sku', () => {
  describe('construction', () => {
    it('should construct with valid SKU', () => {
      const sku = new Sku('SKU-A');
      expect(sku.value).toBe('SKU-A');
    });

    it('should trim whitespace', () => {
      const sku = new Sku('  SKU-A  ');
      expect(sku.value).toBe('SKU-A');
    });

    it('should reject empty string', () => {
      expect(() => new Sku('')).toThrow('Sku cannot be empty');
    });

    it('should reject whitespace-only string', () => {
      expect(() => new Sku('   ')).toThrow('Sku cannot be empty');
    });
  });

  describe('equals', () => {
    it('should return true for equal SKUs', () => {
      const sku1 = new Sku('SKU-A');
      const sku2 = new Sku('SKU-A');
      expect(sku1.equals(sku2)).toBe(true);
    });

    it('should return false for different SKUs', () => {
      const sku1 = new Sku('SKU-A');
      const sku2 = new Sku('SKU-B');
      expect(sku1.equals(sku2)).toBe(false);
    });
  });

  describe('toString', () => {
    it('should return the SKU value', () => {
      const sku = new Sku('SKU-A');
      expect(sku.toString()).toBe('SKU-A');
    });
  });

  describe('use in Map', () => {
    it('should work as Map key', () => {
      const sku1 = new Sku('SKU-A');
      const sku2 = new Sku('SKU-B');
      const map = new Map<Sku, number>();
      map.set(sku1, 10);
      map.set(sku2, 20);
      expect(map.get(sku1)).toBe(10);
      expect(map.get(sku2)).toBe(20);
    });
  });
});

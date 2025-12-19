import { describe, it, expect } from 'vitest';
import { Quantity } from './Quantity';

describe('Quantity', () => {
  describe('construction', () => {
    it('should construct with valid quantity', () => {
      const qty = new Quantity(5);
      expect(qty.value).toBe(5);
    });

    it('should allow zero quantity', () => {
      const qty = new Quantity(0);
      expect(qty.value).toBe(0);
    });

    it('should reject negative quantity', () => {
      expect(() => new Quantity(-1)).toThrow('Quantity must be non-negative');
    });

    it('should reject non-integer quantity', () => {
      expect(() => new Quantity(1.5)).toThrow('Quantity must be an integer');
    });
  });

  describe('value getter', () => {
    it('should return the quantity value', () => {
      const qty = new Quantity(10);
      expect(qty.value).toBe(10);
    });
  });

  describe('toString', () => {
    it('should format as string', () => {
      const qty = new Quantity(5);
      expect(qty.toString()).toBe('5');
    });

    it('should format zero correctly', () => {
      const qty = new Quantity(0);
      expect(qty.toString()).toBe('0');
    });
  });
});

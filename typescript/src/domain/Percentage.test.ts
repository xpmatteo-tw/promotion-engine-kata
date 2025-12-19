import { describe, it, expect } from 'vitest';
import { Percentage } from './Percentage';

describe('Percentage', () => {
  describe('construction', () => {
    it('should construct with valid percentage', () => {
      const pct = new Percentage(15);
      expect(pct.value).toBe(15);
    });

    it('should allow 0 percent', () => {
      const pct = new Percentage(0);
      expect(pct.value).toBe(0);
    });

    it('should allow 100 percent', () => {
      const pct = new Percentage(100);
      expect(pct.value).toBe(100);
    });

    it('should reject negative percentage', () => {
      expect(() => new Percentage(-1)).toThrow('Percentage must be between 0 and 100');
    });

    it('should reject percentage over 100', () => {
      expect(() => new Percentage(101)).toThrow('Percentage must be between 0 and 100');
    });
  });

  describe('asDecimal', () => {
    it('should convert 15% to 0.15', () => {
      const pct = new Percentage(15);
      expect(pct.asDecimal()).toBe(0.15);
    });

    it('should convert 0% to 0', () => {
      const pct = new Percentage(0);
      expect(pct.asDecimal()).toBe(0);
    });

    it('should convert 100% to 1', () => {
      const pct = new Percentage(100);
      expect(pct.asDecimal()).toBe(1);
    });

    it('should convert 50% to 0.5', () => {
      const pct = new Percentage(50);
      expect(pct.asDecimal()).toBe(0.5);
    });
  });

  describe('toString', () => {
    it('should format as percentage string', () => {
      const pct = new Percentage(15);
      expect(pct.toString()).toBe('15%');
    });

    it('should format 0% correctly', () => {
      const pct = new Percentage(0);
      expect(pct.toString()).toBe('0%');
    });

    it('should format 100% correctly', () => {
      const pct = new Percentage(100);
      expect(pct.toString()).toBe('100%');
    });
  });
});

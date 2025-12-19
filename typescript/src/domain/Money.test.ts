import { describe, it, expect } from 'vitest';
import { Money } from './Money';

describe('Money', () => {
  describe('construction', () => {
    it('should construct from euros', () => {
      const money = Money.euros('10.50');
      expect(money.amount.toFixed(2)).toBe('10.50');
    });

    it('should construct from cents', () => {
      const money = Money.cents(1050);
      expect(money.amount.toFixed(2)).toBe('10.50');
    });

    it('should quantize to 2 decimals', () => {
      const money = Money.euros('10.12345');
      expect(money.amount.toFixed(2)).toBe('10.12');
    });

    describe('ROUND_HALF_UP rounding', () => {
      it('should round .235 up to .24', () => {
        const money = Money.euros('1.235');
        expect(money.amount.toFixed(2)).toBe('1.24');
      });

      it('should round .225 up to .23 (half-up)', () => {
        const money = Money.euros('1.225');
        expect(money.amount.toFixed(2)).toBe('1.23');
      });

      it('should round .224 down to .22', () => {
        const money = Money.euros('1.224');
        expect(money.amount.toFixed(2)).toBe('1.22');
      });

      it('should round .215 up to .22', () => {
        const money = Money.euros('1.215');
        expect(money.amount.toFixed(2)).toBe('1.22');
      });

      it('should round .214 down to .21', () => {
        const money = Money.euros('1.214');
        expect(money.amount.toFixed(2)).toBe('1.21');
      });
    });
  });

  describe('arithmetic', () => {
    it('should add two amounts', () => {
      const m1 = Money.euros('10.50');
      const m2 = Money.euros('5.25');
      const result = m1.add(m2);
      expect(result.amount.toFixed(2)).toBe('15.75');
    });

    it('should subtract two amounts', () => {
      const m1 = Money.euros('10.50');
      const m2 = Money.euros('5.25');
      const result = m1.subtract(m2);
      expect(result.amount.toFixed(2)).toBe('5.25');
    });

    it('should multiply by integer', () => {
      const m = Money.euros('10.50');
      const result = m.multiply(3);
      expect(result.amount.toFixed(2)).toBe('31.50');
    });

    it('should multiply by decimal and round', () => {
      const m = Money.euros('10.00');
      const result = m.multiply('0.15');
      expect(result.amount.toFixed(2)).toBe('1.50');
    });

    it('should handle multiplication rounding correctly', () => {
      const m = Money.euros('10.00');
      const result = m.multiply('0.125');
      expect(result.amount.toFixed(2)).toBe('1.25');
    });
  });

  describe('comparisons', () => {
    it('should compare equality', () => {
      const m1 = Money.euros('10.50');
      const m2 = Money.euros('10.50');
      const m3 = Money.euros('5.25');
      expect(m1.equals(m2)).toBe(true);
      expect(m1.equals(m3)).toBe(false);
    });

    it('should compare less than', () => {
      const m1 = Money.euros('5.25');
      const m2 = Money.euros('10.50');
      expect(m1.lessThan(m2)).toBe(true);
      expect(m2.lessThan(m1)).toBe(false);
      expect(m1.lessThan(m1)).toBe(false);
    });

    it('should compare greater than', () => {
      const m1 = Money.euros('10.50');
      const m2 = Money.euros('5.25');
      expect(m1.greaterThan(m2)).toBe(true);
      expect(m2.greaterThan(m1)).toBe(false);
      expect(m1.greaterThan(m1)).toBe(false);
    });

    it('should compare less than or equal', () => {
      const m1 = Money.euros('5.25');
      const m2 = Money.euros('10.50');
      const m3 = Money.euros('5.25');
      expect(m1.lessThanOrEqual(m2)).toBe(true);
      expect(m1.lessThanOrEqual(m3)).toBe(true);
      expect(m2.lessThanOrEqual(m1)).toBe(false);
    });

    it('should compare greater than or equal', () => {
      const m1 = Money.euros('10.50');
      const m2 = Money.euros('5.25');
      const m3 = Money.euros('10.50');
      expect(m1.greaterThanOrEqual(m2)).toBe(true);
      expect(m1.greaterThanOrEqual(m3)).toBe(true);
      expect(m2.greaterThanOrEqual(m1)).toBe(false);
    });
  });

  describe('toString', () => {
    it('should format as dollar string', () => {
      const money = Money.euros('10.50');
      expect(money.toString()).toBe('$10.50');
    });

    it('should format zero correctly', () => {
      const money = Money.euros('0');
      expect(money.toString()).toBe('$0.00');
    });

    it('should include leading zero for cents only', () => {
      const money = Money.euros('0.50');
      expect(money.toString()).toBe('$0.50');
    });
  });

  describe('immutability', () => {
    it('should return new instance from add', () => {
      const m1 = Money.euros('10.00');
      const m2 = Money.euros('5.00');
      const result = m1.add(m2);
      expect(result).not.toBe(m1);
      expect(result).not.toBe(m2);
      expect(m1.amount.toFixed(2)).toBe('10.00');
      expect(m2.amount.toFixed(2)).toBe('5.00');
    });

    it('should return new instance from subtract', () => {
      const m1 = Money.euros('10.00');
      const m2 = Money.euros('5.00');
      const result = m1.subtract(m2);
      expect(result).not.toBe(m1);
      expect(result).not.toBe(m2);
    });

    it('should return new instance from multiply', () => {
      const m = Money.euros('10.00');
      const result = m.multiply(2);
      expect(result).not.toBe(m);
      expect(m.amount.toFixed(2)).toBe('10.00');
    });
  });
});

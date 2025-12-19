import { describe, it, expect } from 'vitest';
import { PromotionId } from './PromotionId';

describe('PromotionId', () => {
  describe('construction', () => {
    it('should construct with valid promotion ID', () => {
      const id = new PromotionId('PROMO-123');
      expect(id.value).toBe('PROMO-123');
    });

    it('should trim whitespace', () => {
      const id = new PromotionId('  PROMO-123  ');
      expect(id.value).toBe('PROMO-123');
    });

    it('should reject empty string', () => {
      expect(() => new PromotionId('')).toThrow('PromotionId cannot be empty');
    });

    it('should reject whitespace-only string', () => {
      expect(() => new PromotionId('   ')).toThrow('PromotionId cannot be empty');
    });
  });

  describe('equals', () => {
    it('should return true for equal promotion IDs', () => {
      const id1 = new PromotionId('PROMO-123');
      const id2 = new PromotionId('PROMO-123');
      expect(id1.equals(id2)).toBe(true);
    });

    it('should return false for different promotion IDs', () => {
      const id1 = new PromotionId('PROMO-123');
      const id2 = new PromotionId('PROMO-456');
      expect(id1.equals(id2)).toBe(false);
    });
  });

  describe('toString', () => {
    it('should return the promotion ID value', () => {
      const id = new PromotionId('PROMO-123');
      expect(id.toString()).toBe('PROMO-123');
    });
  });
});

import { describe, it, expect } from 'vitest';
import { PricingContext } from './PricingContext';

describe('PricingContext', () => {
  describe('construction', () => {
    it('should construct with valid fields', () => {
      const now = new Date('2025-12-18T10:00:00Z');
      const tags = new Set(['new', 'gold']);
      const context = new PricingContext(now, 'online', 'CUST123', tags);

      expect(context.now).toBe(now);
      expect(context.channel).toBe('online');
      expect(context.customerId).toBe('CUST123');
      expect(context.customerTags).toBe(tags);
    });
  });

  describe('hasTag', () => {
    it('should return true for existing tag', () => {
      const tags = new Set(['new', 'gold']);
      const context = new PricingContext(new Date(), 'online', 'CUST123', tags);

      expect(context.hasTag('new')).toBe(true);
      expect(context.hasTag('gold')).toBe(true);
    });

    it('should return false for non-existing tag', () => {
      const tags = new Set(['new', 'gold']);
      const context = new PricingContext(new Date(), 'online', 'CUST123', tags);

      expect(context.hasTag('silver')).toBe(false);
      expect(context.hasTag('platinum')).toBe(false);
    });

    it('should work with empty tags', () => {
      const tags = new Set<string>();
      const context = new PricingContext(new Date(), 'online', 'CUST123', tags);

      expect(context.hasTag('new')).toBe(false);
    });
  });
});

// ABOUTME: Promotion identifier with validation
// ABOUTME: Type-safe wrapper for promotion ID strings

export class PromotionId {
  private readonly _value: string;

  constructor(value: string) {
    if (!value || value.trim().length === 0) {
      throw new Error('PromotionId cannot be empty');
    }
    this._value = value.trim();
  }

  get value(): string {
    return this._value;
  }

  equals(other: PromotionId): boolean {
    return this._value === other._value;
  }

  toString(): string {
    return this._value;
  }
}

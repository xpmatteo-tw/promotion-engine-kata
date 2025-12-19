// ABOUTME: Stock keeping unit identifier with validation
// ABOUTME: Type-safe wrapper for SKU strings

export class Sku {
  private readonly _value: string;

  constructor(value: string) {
    if (!value || value.trim().length === 0) {
      throw new Error('Sku cannot be empty');
    }
    this._value = value.trim();
  }

  get value(): string {
    return this._value;
  }

  equals(other: Sku): boolean {
    return this._value === other._value;
  }

  toString(): string {
    return this._value;
  }
}

// ABOUTME: Non-negative quantity value type with validation
// ABOUTME: Represents item quantities in shopping carts

export class Quantity {
  private readonly _value: number;

  constructor(value: number) {
    if (value < 0) {
      throw new Error(`Quantity must be non-negative, got ${value}`);
    }
    if (!Number.isInteger(value)) {
      throw new Error(`Quantity must be an integer, got ${value}`);
    }
    this._value = value;
  }

  get value(): number {
    return this._value;
  }

  toString(): string {
    return `${this._value}`;
  }
}

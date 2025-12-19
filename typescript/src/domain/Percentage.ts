// ABOUTME: Percentage value type with range validation (0-100)
// ABOUTME: Provides conversion to decimal for calculations

export class Percentage {
  private readonly _value: number;

  constructor(value: number) {
    if (value < 0 || value > 100) {
      throw new Error(`Percentage must be between 0 and 100, got ${value}`);
    }
    this._value = value;
  }

  get value(): number {
    return this._value;
  }

  asDecimal(): number {
    return this._value / 100;
  }

  toString(): string {
    return `${this._value}%`;
  }
}

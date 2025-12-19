// ABOUTME: Immutable monetary value using decimal.js for precision
// ABOUTME: Provides arithmetic operations with ROUND_HALF_UP rounding

import Decimal from 'decimal.js';

export class Money {
  private readonly _amount: Decimal;

  private constructor(amount: Decimal) {
    // Quantize to 2 decimals using ROUND_HALF_UP
    this._amount = amount.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  static euros(value: Decimal.Value): Money {
    return new Money(new Decimal(value));
  }

  static cents(cents: number): Money {
    return new Money(new Decimal(cents).div(100));
  }

  get amount(): Decimal {
    return this._amount;
  }

  add(other: Money): Money {
    return new Money(this._amount.add(other._amount));
  }

  subtract(other: Money): Money {
    return new Money(this._amount.sub(other._amount));
  }

  multiply(value: Decimal.Value): Money {
    return new Money(this._amount.mul(value));
  }

  equals(other: Money): boolean {
    return this._amount.equals(other._amount);
  }

  lessThan(other: Money): boolean {
    return this._amount.lessThan(other._amount);
  }

  greaterThan(other: Money): boolean {
    return this._amount.greaterThan(other._amount);
  }

  lessThanOrEqual(other: Money): boolean {
    return this._amount.lessThanOrEqualTo(other._amount);
  }

  greaterThanOrEqual(other: Money): boolean {
    return this._amount.greaterThanOrEqualTo(other._amount);
  }

  toString(): string {
    return `$${this._amount.toFixed(2)}`;
  }
}

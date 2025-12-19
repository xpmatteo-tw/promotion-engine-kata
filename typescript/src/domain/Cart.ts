// ABOUTME: Shopping cart containing line items
// ABOUTME: Calculates subtotals and manages cart state

import { LineItem } from './LineItem';
import { Money } from './Money';

export class Cart {
  private readonly _lines: readonly LineItem[];

  constructor(lines: LineItem[]) {
    // Defensive copy and freeze
    this._lines = Object.freeze([...lines]);
  }

  get lines(): readonly LineItem[] {
    return this._lines;
  }

  subtotal(): Money {
    if (this._lines.length === 0) {
      return Money.euros(0);
    }

    return this._lines.map((line) => line.subtotal()).reduce((acc, curr) => acc.add(curr));
  }
}

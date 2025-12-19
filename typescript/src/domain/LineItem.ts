// ABOUTME: Line item in a shopping cart with product, quantity, and unit price
// ABOUTME: Calculates subtotals for individual cart lines

import { Product } from './Product';
import { Quantity } from './Quantity';
import { Money } from './Money';

export class LineItem {
  constructor(
    public readonly product: Product,
    public readonly quantity: Quantity,
    public readonly unitPrice: Money
  ) {}

  subtotal(): Money {
    return this.unitPrice.multiply(this.quantity.value);
  }
}

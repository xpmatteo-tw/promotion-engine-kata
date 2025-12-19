// ABOUTME: Product catalog entry with SKU, name, and category
// ABOUTME: Immutable domain model representing a product in the catalog

import { Sku } from './Sku';

export class Product {
  constructor(
    public readonly sku: Sku,
    public readonly name: string,
    public readonly category: string
  ) {}
}

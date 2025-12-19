// ABOUTME: Context information for pricing calculations
// ABOUTME: Includes timestamp, channel, customer ID, and customer tags

export class PricingContext {
  constructor(
    public readonly now: Date,
    public readonly channel: string,
    public readonly customerId: string,
    public readonly customerTags: ReadonlySet<string>
  ) {}

  hasTag(tag: string): boolean {
    return this.customerTags.has(tag);
  }
}

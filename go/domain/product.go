// ABOUTME: Product domain type representing a product in the catalog.
// ABOUTME: Contains product identification, name, and categorization.
package domain

// Product represents a product in the catalog.
type Product struct {
	Sku      Sku
	Name     string
	Category string
}

// NewProduct creates a new Product.
func NewProduct(sku Sku, name, category string) Product {
	return Product{
		Sku:      sku,
		Name:     name,
		Category: category,
	}
}

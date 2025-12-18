// ABOUTME: Unit tests for the Product domain type.
// ABOUTME: Verifies product creation and field assignment.
package promotionengine

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewProduct(t *testing.T) {
	sku := MustSku("APPLE")
	product := NewProduct(sku, "Apple", "Fruit")

	assert.Equal(t, sku, product.Sku)
	assert.Equal(t, "Apple", product.Name)
	assert.Equal(t, "Fruit", product.Category)
}

func TestProductStructLiteral(t *testing.T) {
	product := Product{
		Sku:      MustSku("BANANA"),
		Name:     "Banana",
		Category: "Fruit",
	}

	assert.Equal(t, "BANANA", product.Sku.Value)
	assert.Equal(t, "Banana", product.Name)
	assert.Equal(t, "Fruit", product.Category)
}

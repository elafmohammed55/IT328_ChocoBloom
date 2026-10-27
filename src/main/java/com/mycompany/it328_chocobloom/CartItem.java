
package com.mycompany.it328_chocobloom;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid cart item");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be positive"
            );
        }
        this.quantity = quantity;
    }

    // Phase 1: Adjust cart quantity to available stock
    public void adjustToAvailableStock() {
        int availableStock = product.getStock();

        if (quantity > availableStock) {
            quantity = availableStock;
        }
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}

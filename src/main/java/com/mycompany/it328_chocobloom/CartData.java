package com.mycompany.it328_chocobloom;

import java.util.ArrayList;
import java.util.List;

/**
 * Member 2 - the items the user selected (UI side only).
 * Each line is a CartItem (team's class). Products are matched by id.
 * It never changes the store stock; the server manages stock.
 */
public class CartData {

    private final List<CartItem> items = new ArrayList<>();

    /** Add a product; if it is already in the cart, increase its quantity. */
    public void addItem(Product product, int quantity) {
        CartItem existing = find(product.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    /** Quantity of this product already in the cart (0 if not added). */
    public int getQuantity(int productId) {
        CartItem item = find(productId);
        return item == null ? 0 : item.getQuantity();
    }

    /**
     * Called when the team code gives an updated stock for a product.
     * If the cart has more than the available stock, the quantity is reduced
     * (and the item is removed if the stock is 0).
     */
    public void updateAvailableStock(int productId, int availableStock) {
        CartItem item = find(productId);
        if (item == null) {
            return;
        }
        if (availableStock <= 0) {
            items.remove(item);
        } else if (item.getQuantity() > availableStock) {
            item.setQuantity(availableStock);
        }
    }

    public List<CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    private CartItem find(int productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                return item;
            }
        }
        return null;
    }
}

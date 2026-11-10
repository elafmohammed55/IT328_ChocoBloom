package com.mycompany.it328_chocobloom;

import java.util.ArrayList;
import java.util.List;

public class CartData {

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        CartItem existing = find(product.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public int getQuantity(int productId) {
        CartItem item = find(productId);
        return item == null ? 0 : item.getQuantity();
    }

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

    public void clear() {
        items.clear();
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

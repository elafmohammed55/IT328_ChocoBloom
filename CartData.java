package com.mycompany.it328_481project;

import java.util.ArrayList;
import java.util.List;

/**
 * Member 2 - the items the user selected (UI side only).
 * TEMPORARY holder until the team decides where the cart data lives.
 * It never changes the store stock; the server manages stock.
 */
public class CartData {

    /** One line in the cart. */
    public static class Item {
        public final String name;
        public final double price;
        public int quantity;

        Item(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public double getSubtotal() {
            return price * quantity;
        }
    }

    private final List<Item> items = new ArrayList<>();

    /** Add a product; if it is already in the cart, increase its quantity. */
    public void addItem(String name, double price, int quantity) {
        Item existing = find(name);
        if (existing != null) {
            existing.quantity += quantity;
        } else {
            items.add(new Item(name, price, quantity));
        }
    }

    /** Quantity of this product already in the cart (0 if not added). */
    public int getQuantity(String name) {
        Item item = find(name);
        return item == null ? 0 : item.quantity;
    }

    /**
     * Called when the team code gives an updated stock for a product.
     * If the cart has more than the available stock, the quantity is reduced
     * (and the item is removed if the stock is 0).
     */
    public void updateAvailableStock(String name, int availableStock) {
        Item item = find(name);
        if (item == null) {
            return;
        }
        if (availableStock <= 0) {
            items.remove(item);
        } else if (item.quantity > availableStock) {
            item.quantity = availableStock;
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    private Item find(String name) {
        for (Item item : items) {
            if (item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }
}

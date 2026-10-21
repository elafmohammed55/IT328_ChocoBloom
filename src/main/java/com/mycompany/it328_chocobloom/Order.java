package com.mycompany.it328_chocobloom;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { CONFIRMED, CANCELLED }

    private final int orderId;
    private final String username;
    private final LocalDateTime timestamp;
    private final List<OrderItem> items;
    private Status status;

    public Order(int orderId, String username, List<OrderItem> items) {
        if (orderId < 0 || username == null || username.trim().isEmpty()
                || items == null || items.isEmpty()
                || items.contains(null)) {
            throw new IllegalArgumentException("Invalid order");
        }

        this.orderId = orderId;
        this.username = username;
        this.items = Collections.unmodifiableList(
            new ArrayList<>(items)
        );
        this.timestamp = LocalDateTime.now();
        this.status = Status.CONFIRMED;
    }

    public int getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<OrderItem> getItems() { return items; }

    public synchronized Status getStatus() {
        return status;
    }

    public double getTotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    public synchronized boolean markCancelled() {
        if (status == Status.CANCELLED) {
            return false;
        }
        status = Status.CANCELLED;
        return true;
    }
}

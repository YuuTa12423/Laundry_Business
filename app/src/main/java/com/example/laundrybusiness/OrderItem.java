// File: OrderItem.java

package com.example.laundrybusiness;

public class OrderItem {
    public String id;
    public String dateStatus;
    public String price;
    // MODIFIED: Replace boolean isDelivered with a detailed status string
    public String currentStatus;

    public OrderItem(String id, String dateStatus, String price, String currentStatus) {
        this.id = id;
        this.dateStatus = dateStatus;
        this.price = price;
        // Updated constructor signature
        this.currentStatus = currentStatus;
    }
}
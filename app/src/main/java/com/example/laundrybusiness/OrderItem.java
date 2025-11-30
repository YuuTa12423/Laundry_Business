package com.example.laundrybusiness;

public class OrderItem {
    public String id;
    public String dateStatus;
    public String price;
    public boolean isDelivered;

    public OrderItem(String id, String dateStatus, String price, boolean isDelivered) {
        this.id = id;
        this.dateStatus = dateStatus;
        this.price = price;
        this.isDelivered = isDelivered;
    }
}
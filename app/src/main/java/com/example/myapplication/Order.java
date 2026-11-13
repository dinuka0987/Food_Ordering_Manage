package com.example.myapplication;

import java.math.BigDecimal;

public class Order {
    public static final String[] STATUSES = {"Pending", "Preparing", "Ready", "Completed", "Cancelled"};
    private final long id, unitPriceCents;
    private final String orderNumber, customerName, phone, foodItem, status;
    private final int quantity;

    public Order(long id, String orderNumber, String customerName, String phone,
                 String foodItem, int quantity, long unitPriceCents, String status) {
        if (quantity < 1 || quantity > 9999 || unitPriceCents < 1 || unitPriceCents > 99999999L)
            throw new IllegalArgumentException("Invalid quantity or unit price");
        boolean validStatus = false;
        for (String value : STATUSES) if (value.equals(status)) validStatus = true;
        if (!validStatus) throw new IllegalArgumentException("Invalid order status");
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.phone = phone;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
        this.status = status;
    }

    public long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getFoodItem() { return foodItem; }
    public int getQuantity() { return quantity; }
    public long getUnitPriceCents() { return unitPriceCents; }
    public long getTotalCents() { return unitPriceCents * quantity; }
    public String getStatus() { return status; }
    public static String formatMoney(long cents) { return BigDecimal.valueOf(cents, 2).toPlainString(); }
    public static long parsePrice(String value) {
        if (!value.matches("[0-9]{1,6}(\\.[0-9]{1,2})?"))
            throw new IllegalArgumentException("Enter a price with up to two decimal places");
        long cents = new BigDecimal(value).movePointRight(2).longValueExact();
        if (cents < 1 || cents > 99999999L) throw new IllegalArgumentException("Price must be 0.01 to 999999.99");
        return cents;
    }
}
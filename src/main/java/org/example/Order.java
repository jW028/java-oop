package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String customerId;
    private List<CartItem> items;
    private double totalAmount;
    
    // Remove the problematic annotations for now
    private LocalDateTime orderDate;
    
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    
    // Default constructor for Jackson
    public Order() {
        this.items = new ArrayList<>();
    }
    
    public Order(String customerId, List<CartItem> items, double totalAmount, 
                String shippingAddress, String paymentMethod) {
        this.orderId = generateOrderId();
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = "Pending";
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }
    
    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Getters and setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    @JsonIgnore
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderDate.format(formatter);
    }
    
    public void displayOrderDetails() {
        System.out.println("\n=== Order Details ===");
        System.out.println("Order ID: " + orderId);
        System.out.println("Order Date: " + getFormattedOrderDate());
        System.out.println("Status: " + status);
        System.out.println("Shipping Address: " + shippingAddress);
        System.out.println("Payment Method: " + paymentMethod);
        
        System.out.println("\nItems:");
        for (CartItem item : items) {
            System.out.println("- " + item.toString());
        }
        
        System.out.println("\nTotal Amount: $" + String.format("%.2f", totalAmount));
    }
}
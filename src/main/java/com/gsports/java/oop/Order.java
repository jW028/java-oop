package com.gsports.java.oop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;

public class Order {

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        COMPLETED,
        CANCELLED
    }
    
    private String orderId;
    private String customerId;
    private ArrayList<CartItem> items;
    private LocalDateTime orderDate;
    private double totalAmount;      // Total Amount before tax
    private double taxAmount;     // Tax amount
    private double finalAmount;      // Paid amount including tax
    private static double taxRate = 0.06; // Default tax rate (6%)

    @JsonBackReference("payment-order")
    private Payment payment;
    
    private String shippingAddress;
    private OrderStatus status;
    private LocalDateTime lastUpdated;

    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.lastUpdated = LocalDateTime.now();
    }

    public Order(String orderId, String customerId, List<CartItem> items, double totalAmount, 
                String shippingAddress, Payment payment) {
                    this.orderId = orderId;;
                    this.customerId = customerId;
                    this.items = new ArrayList<>(items);
                    this.orderDate = LocalDateTime.now();
                    this.totalAmount = totalAmount;
                    this.taxAmount = totalAmount * taxRate;
                    this.finalAmount = totalAmount + taxAmount;
                    this.shippingAddress = shippingAddress;
                    this.payment = payment;     
                    this.status = OrderStatus.PENDING;
                    this.lastUpdated = LocalDateTime.now(); 
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
    
    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
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
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.lastUpdated = LocalDateTime.now();
    }

    @JsonIgnore
    public String getStatusDisplay() {
        switch(status) {
            case PENDING:
                return "Pending";
            case PROCESSING:
                return "Processing";
            case SHIPPED:
                return "Shipped";
            case DELIVERED:
                return "Delivered";
            case COMPLETED:
                return "Completed";
            case CANCELLED:
                return "Canceled";
            default:
                return "Unknown";
        }
    }

    public boolean progressStatus() {
        switch(status) {
            case PENDING:
                status = OrderStatus.PROCESSING;
                break;
            case PROCESSING:
                status = OrderStatus.SHIPPED;
                break;
            case SHIPPED:
                status = OrderStatus.DELIVERED;
                break;
            case DELIVERED:
                status = OrderStatus.COMPLETED;
                break;
            default:
                return false; // Can't progress from COMPLETED or CANCELED
        }
        this.lastUpdated = LocalDateTime.now();
        return true;
    }

    // Method to cancel an order (only if not shipped yet)
    public boolean cancelOrder() {
        if (status == OrderStatus.PENDING || status == OrderStatus.PROCESSING) {
            status = OrderStatus.CANCELLED;
            this.lastUpdated = LocalDateTime.now();
            return true;
        }
        return false; // Can't cancel if already shipped
    }

    @JsonIgnore
    public String getFormattedLastUpdated() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastUpdated.format(formatter);
    }
    
    
    @JsonIgnore
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderDate.format(formatter);
    }
}

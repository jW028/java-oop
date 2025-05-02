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
        PAID,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        COMPLETED,
        CANCELLED,
        REFUNDED,
        REFUND_REQUESTED,
    }
    
    private String orderId;
    private String customerId;
    private ArrayList<CartItem> items;
    private LocalDateTime orderDate;
    private double totalAmount;      // Total Amount before tax
    private double taxAmount;     // Tax amount
    private double finalAmount;      // Paid amount including tax
    private static double TAX_RATE = 0.06; // Default tax rate (6%)

    @JsonBackReference("payment-order")
    private Payment payment;
    
    private String shippingAddress;
    private OrderStatus status;
    private LocalDateTime lastUpdated;
    private LocalDateTime scheduledStatusUpdateTime;
    private OrderStatus scheduledStatus;
    private static final int DEFAULT_REFUND_WINDOW_MINUTES = 15;

    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.lastUpdated = LocalDateTime.now();
    }

    public Order(String orderId, String customerId, List<CartItem> items, double totalAmount, 
                String shippingAddress, Payment payment) {
                    this.orderId = orderId;
                    this.customerId = customerId;
                    this.items = new ArrayList<>(items);
                    this.orderDate = LocalDateTime.now();
                    this.totalAmount = totalAmount;
                    this.taxAmount = totalAmount * TAX_RATE;
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
   
    /**
     * Schedules a status update to occur after the specified number of minutes
     * @param newStatus The status to update to
     * @param minutes The number of minutes after which the status should be updated
     */
    public void scheduleStatusUpdate(OrderStatus newStatus, int minutes) {
        this.scheduledStatus = newStatus;
        this.scheduledStatusUpdateTime = LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * Gets the remaining time in seconds until the scheduled status update
     * @return Remaining time in seconds, or 0 if no update is scheduled or time has passed
     */
    public long getRemainingTimeForStatusUpdate() {
        if (scheduledStatusUpdateTime == null) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(scheduledStatusUpdateTime)) {
            // If the scheduled time has passed, update the status and return 0
            if (scheduledStatus != null) {
                updateStatus(scheduledStatus);
                scheduledStatus = null;
                scheduledStatusUpdateTime = null;
            }
            return 0;
        }
        
        // Calculate remaining time in seconds
        return java.time.Duration.between(now, scheduledStatusUpdateTime).getSeconds();
    }

    /**
     * Gets the tax rate used for calculating order taxes
     * @return The tax rate as a decimal (e.g., 0.06 for 6%)
     */
    @JsonIgnore
    public static double getTaxRate() {
        return TAX_RATE;
    }
    
    /**
     * Sets the tax rate
     * @param rate The new tax rate as a decimal (e.g., 0.06 for 6%)
     */
    public static void setTaxRate(double rate) {
        TAX_RATE = rate;
    }
    
    /**
     * Gets the remaining time in seconds for the refund window
     * @return Remaining time in seconds, or 0 if refund window has expired
     */
    public long getRemainingRefundTime() {
        // For PENDING orders, refund window is DEFAULT_REFUND_WINDOW_MINUTES from order creation
        if (status == OrderStatus.PENDING) {
            LocalDateTime refundWindowEnd = orderDate.plusMinutes(DEFAULT_REFUND_WINDOW_MINUTES);
            LocalDateTime now = LocalDateTime.now();
            
            if (now.isAfter(refundWindowEnd)) {
                return 0;
            }
            
            return java.time.Duration.between(now, refundWindowEnd).getSeconds();
        } 
        // For DELIVERED orders, allow refund for 7 days
        else if (status == OrderStatus.DELIVERED) {
            // Find when the order was marked as delivered
            LocalDateTime deliveredTime = lastUpdated; // Assuming lastUpdated was set when status changed to DELIVERED
            LocalDateTime refundWindowEnd = deliveredTime.plusDays(7);
            LocalDateTime now = LocalDateTime.now();
            
            if (now.isAfter(refundWindowEnd)) {
                return 0;
            }
            
            return java.time.Duration.between(now, refundWindowEnd).getSeconds();
        }
        
        // For other statuses, no refund window
        return 0;
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
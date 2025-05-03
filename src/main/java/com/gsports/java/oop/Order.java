package com.gsports.java.oop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsports.java.oop.Order.OrderStatus;

public class Order {

    public enum OrderStatus {
        PENDING,
        PAID,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        COMPLETED,
        CANCELLED,
        CANCEL_REQUESTED,
        REFUND_REQUESTED,
    }
    
    private String orderId;
    private Customer customer;
    private ArrayList<CartItem> items;
    private LocalDateTime orderDate;
    private double totalAmount;      // Total Amount before tax
    private double taxAmount;     // Tax amount
    private double finalAmount;      // Paid amount including tax
    public static double TAX_RATE = 0.06; // Default tax rate (6%)

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

    public Order(String orderId, Customer customer, List<CartItem> items, double totalAmount, 
                String shippingAddress, Payment payment) {
                    this.orderId = orderId;
                    this.customer = customer;
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
    
    public Customer getCustomer() {
        return this.customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    // Simple static refund window: 15 minutes from order creation
    LocalDateTime refundWindowEnd = orderDate.plusMinutes(DEFAULT_REFUND_WINDOW_MINUTES);
    LocalDateTime now = LocalDateTime.now();
    
    if (now.isAfter(refundWindowEnd)) {
        return 0;
    }
    
    return java.time.Duration.between(now, refundWindowEnd).getSeconds();
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
                return "Cancelled";
            case CANCEL_REQUESTED:
                return "Cancellation Requested";
            case PAID:
                return "Paid";
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

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        List<Payment> payments = JsonDataHandler.getPaymentsList();
        for (Payment payment : payments) {
            System.out.println("Payment Order ID: " + payment.getOrderId());
            if (payment.getOrderId().equals(this.orderId)) {
                this.payment = payment;
                break;
            }
        }

        // Receipt header with company logo
        receipt.append("\n┌─────────────────────────────────────────────────────────────────┐\n");
        receipt.append("│                          G S P O R T S                          │\n");
        receipt.append("│                   Premium Electronics Retailer                  │\n");
        receipt.append("├─────────────────────────────────────────────────────────────────┤\n");

        // Order information
        receipt.append(String.format("│ Order ID : %-52s │\n", this.orderId));
        receipt.append(String.format("│ Date     : %-52s │\n", getFormattedOrderDate()));
        receipt.append(String.format("│ Customer : %-52s │\n", this.customer.getUsername()));
        receipt.append("├─────────────────────────────────────────────────────────────────┤\n");

        // Shipping address
        String formattedAddress = formatMultilineField(this.shippingAddress, 63);
        receipt.append("│ Shipping Address:                                               │\n");
        receipt.append(formattedAddress);
        receipt.append("├─────────────────────────────────────────────────────────────────┤\n");

        // Items table header
        receipt.append("│                              ITEMS                              │\n");
        receipt.append("├────┬───────────────────────────────┬─────────────┬──────────────┤\n");
        receipt.append(String.format("│ %-2s │ %-29s │ %-11s │ %-12s │\n",
                "#", "Description", "Price", "Subtotal"));
        receipt.append("├────┼───────────────────────────────┼─────────────┼──────────────┤\n");

        // Items
        int itemNum = 1;
        for (CartItem item : this.items) {
            String productName = item.getProduct().getProdName();
            if (productName.length() > 27) {
                productName = productName.substring(0, 24) + "...";
            }

            receipt.append(String.format("│ %-2d │ %-29s │ RM%-9.2f │ RM%-10.2f │\n",
                    itemNum++,
                    productName,
                    item.getProduct().getSellingPrice(),
                    item.getSubtotal()));

            // If there are multiple quantities, show it on the line below
            if (item.getQuantity() > 1) {
                receipt.append(String.format("│    │   x%-26d │             │              │\n",
                        item.getQuantity()));
            }
        }

        // Order summary
        receipt.append("├────┴───────────────────────────────┴─────────────┼──────────────┤\n");
        receipt.append(String.format("│ %48s │ RM%-10.2f │\n", "Subtotal:", this.totalAmount));
        receipt.append(String.format("│ %48s │ RM%-10.2f │\n", "Tax (" + (TAX_RATE * 100) + "%):", this.taxAmount));
        receipt.append("├──────────────────────────────────────────────────┼──────────────┤\n");
        receipt.append(String.format("│ %48s │ RM%-10.2f │\n", "TOTAL:", this.finalAmount));
        receipt.append("└──────────────────────────────────────────────────┴──────────────┘\n");

        // Payment information
        receipt.append("\n┌─────────────────────────────────────────────────────────────────┐\n");
        receipt.append("│                      PAYMENT INFORMATION                        │\n");
        receipt.append("├─────────────────────────────────────────────────────────────────┤\n");

        if (this.payment != null) {
            receipt.append(String.format("│ Method  : %-53s │\n", this.payment.getPaymentMethod()));
            receipt.append(String.format("│ Status  : %-53s │\n", this.payment.getPaymentStatus()));
            receipt.append(String.format("│ Date    : %-53s │\n", this.payment.getFormattedPaymentDate()));

            // Add transaction ID if available
            if (this.payment.getTransactionId() != null && !this.payment.getTransactionId().isEmpty()) {
                receipt.append(String.format("│ Transaction ID: %-47s │\n", this.payment.getTransactionId()));
            }
        } else {
            receipt.append("│ No payment information available                               │\n");
        }
        receipt.append("└─────────────────────────────────────────────────────────────────┘\n");

        // Return policy and customer support
        receipt.append("\n┌─────────────────────────────────────────────────────────────────┐\n");
        receipt.append("│                    RETURN & SUPPORT POLICY                      │\n");
        receipt.append("├─────────────────────────────────────────────────────────────────┤\n");
        receipt.append("│ • Items may be returned within 30 days with receipt             │\n");
        receipt.append("│ • For support, contact us at support@gsports.com.my             │\n");
        receipt.append("│ • Call our hotline: +60 3-1234 5678                             │\n");
        receipt.append("└─────────────────────────────────────────────────────────────────┘\n");

        // Footer
        receipt.append("\n                Thank you for shopping with GSports!                \n");
        receipt.append("                      www.gsports.com.my                          \n\n");

        return receipt.toString();
    }

    private String formatMultilineField(String text, int width) {
        StringBuilder result = new StringBuilder();
        if (text == null || text.isEmpty()) {
            return String.format("│ %-" + width + "s │\n", "");
        }
        
        // Split by newlines first
        String[] paragraphs = text.split("\n");
        for (String paragraph : paragraphs) {
            // Then wrap each paragraph to the width
            int start = 0;
            while (start < paragraph.length()) {
                int end = Math.min(start + width, paragraph.length());
                if (end < paragraph.length() && end > start + 10) {
                    // Try to find a space to break at
                    int breakPoint = paragraph.lastIndexOf(' ', end);
                    if (breakPoint > start) {
                        end = breakPoint;
                    }
                }
                result.append(String.format("│ %-" + width + "s │\n", paragraph.substring(start, end)));
                start = end + (end < paragraph.length() && paragraph.charAt(end) == ' ' ? 1 : 0);
            }
        }
        
        return result.toString();
    }
}
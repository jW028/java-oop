package com.gsports.java.oop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class Payment {
    public enum PaymentStatus {
        PENDING,
        OTP_SENT,
        COMPLETED,
        FAILED,
        FAILED_INVALID_OTP
    };

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        BANK_TRANSFER,
    };

    private String paymentId;
    private String orderId;
    private double amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String otp;

    @JsonBackReference("payment-user")
    private User user;


    public Payment() {
        this.paymentId = generatePaymentId();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Payment(String orderId, double amount, PaymentMethod paymentMethod, User user) {
        this.paymentId = generatePaymentId();
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
        this.transactionId = generateTransactionId();
        this.user = user;
    }

    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12);
    }

    

    public boolean processPayment() {
        // Simulate payment processing
        try {
            System.out.println("Processing payment of RM" + String.format("%.2f", amount) + " via " + paymentMethod + "...");
            System.out.println("Please wait while we secure your transaction...");
            Thread.sleep(1000); // Simulate processing time

            // Generate and send OTP
            if (generateOTP().isEmpty()) {
                System.out.println("Failed to send OTP to your email. Please try again later.");
                this.paymentStatus = PaymentStatus.FAILED;
                return false;
            }

            // OTP was generated successfully
            this.paymentStatus = PaymentStatus.OTP_SENT;
            return true;
        } catch (InterruptedException e) {
            System.out.println("Payment processing interrupted: " + e.getMessage());
            this.paymentStatus = PaymentStatus.FAILED;
            return false;
        }
    }

    public boolean verifyAndCompletePayment(String userProvidedOTP) {
        if (verifyOTP(userProvidedOTP)) {
            this.paymentStatus = PaymentStatus.COMPLETED;
            System.out.println("Payment verification successful!");
            System.out.println("Transaction ID: " + this.transactionId);
            System.out.println("Amount: RM" + String.format("%.2f", amount));
            System.out.println("Date: " + paymentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            return true;
        } else {
            // Invalid OTP
            this.paymentStatus = PaymentStatus.FAILED_INVALID_OTP;
            System.out.println("Invalid OTP. Payment failed.");
            return false;
        }
    }


    private String generateOTP() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otpNumber = 100000 + random.nextInt(900000);
        this.otp = String.valueOf(otpNumber);

        // Simulate sending OTP to phone number
        System.out.println("Generating OTP to send to your registered email: " + user.getEmail());
        System.out.println("Sending email...");

        try {
            Thread.sleep(1500);
            System.out.println("OTP sent successfully!");
            System.out.println("DEMO MODE: Your OTP is: " + otp);

            return this.otp;
        } catch (InterruptedException e) {
            System.out.println("Failed to sent OTP: " + e.getMessage());
            return "";
        }
    }

    private boolean verifyOTP(String input) {
        return input.equals(this.otp);
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n========== THANK YOU FOR YOUR PURCHASE ==========\n");
        receipt.append("Receipt #: ").append(paymentId).append("\n");
        receipt.append("Transaction ID: ").append(transactionId).append("\n");
        receipt.append("Amount Paid: RM").append(String.format("%.2f", amount)).append("\n");
        receipt.append("Payment Method: ").append(paymentMethod).append("\n");
        receipt.append("Payment Date: ").append(paymentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        receipt.append("Payment Status: ").append(paymentStatus.toString()).append("\n");
        receipt.append("Customer: ").append(user.getUsername()).append("\n");
        receipt.append("Thank you for shopping with GSports!\n");
        receipt.append("We hope to see you soon.");
        receipt.append("\n===============================================");

        return receipt.toString();
    }


    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}

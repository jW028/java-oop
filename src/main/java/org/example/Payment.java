package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class Payment {
    private String paymentId;
    private String orderId;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private String transactionId;
    private String otp;
    private User user;

    public Payment() {
        this.paymentId = createUniquePaymentId();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = "Pending";
    }

    public Payment(String orderId, double amount, String paymentMethod, User user) {
        this.paymentId = createUniquePaymentId();
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = "Pending";
        this.transactionId = createUniqueTransactionId();
        this.user = user;
    }

    private String createUniquePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String createUniqueTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12);
    }

    public boolean processPayment() {
        // Simulate payment processing
        try {
            System.out.println("Processing your payment of $" + String.format("%.2f", amount) + " via " + paymentMethod + "...");
            System.out.println("Please wait while we secure your transaction...");
            Thread.sleep(1000); // Simulate processing time
            
            // Generate and send OTP
            if (!sendOTPToEmail()) {
                System.out.println("Failed to send OTP to your email. Please try again later.");
                this.paymentStatus = "Failed";
                return false;
            }
            
            // Verify OTP
            if (!verifyOTP()) {
                System.out.println("OTP verification failed. Payment cancelled.");
                this.paymentStatus = "Failed";
                return false;
            }
            
            // After OTP verification, proceed with payment
            System.out.println("OTP verified successfully! Processing payment...");
            Thread.sleep(1000);
            
            // In a real system, this would connect to a payment gateway
            boolean paymentSuccessful = simulatePaymentGateway();
            
            if (paymentSuccessful) {
                this.paymentStatus = "Completed";
                System.out.println("Great news! Your payment was successful.");
                return true;
            } else {
                this.paymentStatus = "Failed";
                System.out.println("We're sorry, but your payment couldn't be processed at this time. Please try again or use a different payment method.");
                return false;
            }
        } catch (InterruptedException e) {
            this.paymentStatus = "Error";
            System.out.println("Oops! Something went wrong with your payment: " + e.getMessage());
            System.out.println("Please try again in a few moments.");
            return false;
        }
    }
    
    private boolean sendOTPToEmail() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otpNumber = 100000 + random.nextInt(900000);
        this.otp = String.valueOf(otpNumber);
        
        // In a real application, you would send an email here
        // For this demo, we'll simulate sending an email
        System.out.println("Sending OTP to your registered email: " + user.getEmail());
        System.out.println("Simulating email sending...");
        
        try {
            Thread.sleep(1500); // Simulate email sending delay
            
            // For demo purposes, show the OTP in console
            // In a real app, this would only be sent to the email
            System.out.println("OTP sent successfully!");
            System.out.println("DEMO MODE: Your OTP is: " + otp);
            
            return true;
        } catch (InterruptedException e) {
            System.out.println("Failed to send OTP: " + e.getMessage());
            return false;
        }
    }
    
    private boolean verifyOTP() {
        // Instead of creating a new Scanner
        // Scanner scanner = new Scanner(System.in);
        
        // Use the shared scanner
        Scanner scanner = MenuUtils.getScanner();
        
        int attempts = 3;
        
        while (attempts > 0) {
            System.out.print("Please enter the OTP sent to your email: ");
            String enteredOTP = scanner.nextLine().trim();
            
            if (enteredOTP.equals(this.otp)) {
                return true;
            } else {
                attempts--;
                if (attempts > 0) {
                    System.out.println("Incorrect OTP. You have " + attempts + " attempts remaining.");
                } else {
                    System.out.println("You've entered an incorrect OTP too many times.");
                }
            }
        }
        
        // Don't close the scanner here since it's shared
        return false;
    }

    private boolean simulatePaymentGateway() {
        // This is a placeholder for actual payment gateway integration
        // In a real application, this would connect to a payment service
        // For demo purposes, we'll return true 90% of the time
        return Math.random() < 0.9;
    }

    public void generateReceipt() {
        if (!"Completed".equals(paymentStatus)) {
            System.out.println("We can only generate a receipt for completed payments.");
            return;
        }

        System.out.println("\n========== THANK YOU FOR YOUR PURCHASE ==========");
        System.out.println("Receipt #: " + paymentId);
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount Paid: $" + String.format("%.2f", amount));
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Date: " + paymentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")));
        System.out.println("Status: Payment Successful");
        System.out.println("\nThank you for shopping with GSports!");
        System.out.println("We hope to see you again soon.");
        System.out.println("=================================================");
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}
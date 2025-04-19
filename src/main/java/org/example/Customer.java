package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class Customer extends User {

    private String address;
    private String phoneNum;

    private Cart cart;
    private Wishlist wishlist;
    private List<Order> orderHistory;

    // Default constructor
    public Customer() {
        super();
        this.cart = new Cart();
        this.wishlist = new Wishlist();
        this.orderHistory = new ArrayList<>();
    }
    
    // Constructor with parameters
    public Customer(String userID, String username, String email, String password, String address, String phoneNum) {
        super(userID, username, email, password);
        this.address = address;
        this.phoneNum = phoneNum;
        this.cart = new Cart();
        this.wishlist = new Wishlist();
        this.orderHistory = new ArrayList<>();
    }

    // Getters
    public String getAddress() {
        return address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public Cart getCart() {
        return cart;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }
    
    public List<Order> getOrderHistory() {
        if (orderHistory == null) {
            orderHistory = new ArrayList<>();
        }
        return orderHistory;
    }
    
    // Setters
    public void setAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            this.address = address;
        } else {
            throw new IllegalArgumentException("Address cannot be empty");
        }
    }
    
    public void setPhoneNum(String phoneNum) {
        if (phoneNum != null && !phoneNum.trim().isEmpty()) {
            this.phoneNum = phoneNum;
        } else {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
    }
    
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    
    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }
    
    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }
    
    public void addOrder(Order order) {
        if (orderHistory == null) {
            orderHistory = new ArrayList<>();
        }
        orderHistory.add(order);
    }
    
    // Add the missing setUsername method
    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            super.setUsername(username);
        } else {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }
    
    // Add the missing setPassword method
    public void setPassword(String password) {
        if (password != null && password.length() >= 8) {
            super.setPassword(password);
        } else {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
    
    private static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "userID='" + getUserID() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + address + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                '}';
    }
    
    /**
     * Static method to register a new customer
     */
    public static Customer register(String username, String email, String password, String phoneNum, String address) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        if (phoneNum == null || phoneNum.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        
        // Generate a unique customer ID
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create and return a new customer
        return new Customer(customerId, username, email, password, address, phoneNum);
    }
}

package com.gsports.java.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonBackReference;

public class Customer extends User {
    private String address;
    private String phoneNum;

    private Cart cart;
    private Wishlist wishlist;

    @JsonBackReference("customer-orders")
    private List<Order> orderHistory;

    public Customer() {
        super();
        this.cart = new Cart();
        this.wishlist = new Wishlist();
        this.orderHistory = new ArrayList<>();
    }

    public Customer(boolean deserializing) {
        super();
        if (!deserializing) {
            this.cart = new Cart();
            this.wishlist = new Wishlist();
            this.orderHistory = new ArrayList<>();
        }
    }

    public Customer(String username, String email, String password, String address, String phoneNum) {
        super(username, email, password);
        this.address = address;
        this.phoneNum = phoneNum;
        this.cart = new Cart();
        this.wishlist = new Wishlist();
        this.orderHistory = new ArrayList<>();
        this.setUserID(generateCustomerId());
    }
    
    private String generateCustomerId() {
        return "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Setters and getters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        if (phoneNum != null && Pattern.matches("\\d{10,11}", phoneNum)) {
            this.phoneNum = phoneNum;
        } else {
            System.out.println("Invalid phone number format.");
        }
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }   

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public void addOrder(Order order) {
        if (order != null) {
            this.orderHistory.add(order);
        } else {
            System.out.println("Invalid order.");
        }
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

}

package com.gsports.java.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cart {
    private String cartId;
    private String userId;
    private ArrayList<CartItem> items;
    private double totalAmount;

    public Cart() {
        this.cartId = generateCartId();
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public Cart(String userId) {
        this.cartId = generateCartId();
        this.userId = userId;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    private String generateCartId() {
        return "CART-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Setters and getters
    public String getCartId() {
        return cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(Product product, int quantity) {
        if (product == null) {
            System.out.println("Sorry, we couldn't find that product.");
            return;
        }

        if (quantity <= 0) {
            System.out.println("Please select at least one item to add to your cart.");;
            return;
        }

        if (product.getStock() < quantity) {
            System.out.println("Sorry, we only have " + product.getStock() + " of this item in stock.");
            return;
        }

        for (CartItem item : items) {
            if (item.getProduct().getProdID().equals(product.getProdID())) {
                item.setQuantity(item.getQuantity() + quantity);
                recalculateTotal();
                System.out.println("Added " + quantity + " of " + product.getProdName() + " to your cart.");
                return;
            }
        }

        CartItem newItem = new CartItem(product, quantity);
        items.add(newItem);
        recalculateTotal();
        System.out.println("\n" + product.getProdName() + " has been added to your cart!");
    }

    private void recalculateTotal() {
        totalAmount = 0.0;
        for (CartItem item : items) {
            totalAmount += item.getSubtotal();
        }
    }

    public void removeItem(String productId) {
        String removedProdName = "";
        for (CartItem item : items) {
            if (item.getProduct().getProdID().equals(productId)) {
                removedProdName = item.getProduct().getProdName();
                break;
            }
        }

        items.removeIf(item -> item.getProduct().getProdID().equals(productId));
        recalculateTotal();

        if (!removedProdName.isEmpty()) {
            System.out.println(removedProdName = " has been removed from your cart.");
        } else {
            System.out.println("The specified item wasn't in your cart.");
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            recalculateTotal();
            System.out.println("Item removed successfully.");
        } else {
            System.out.println("Invalid item index.");
        }
    }

    public void updateItemQuantity(String productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(productId);
            return;
        }

        for (CartItem item : items) {
            if (item.getProduct().getProdID().equals(productId)) {
                if (item.getProduct().getStock() < newQuantity) {
                    System.out.println("Sorry, we only have " + item.getProduct().getStock() + " of this item in stock ");
                }
                item.setQuantity(newQuantity);
                recalculateTotal();
                System.out.println("Updated " + item.getProduct().getProdName() + " quantity to " + newQuantity);
                return;
            }
        }
        System.out.println("The specified item isn't in your cart yet.");
    }

    public void updateItemQuantity(int index, int newQuantity) {
        if (index >= 0 && index < items.size()) {
            CartItem item = items.get(index);
            item.setQuantity(newQuantity);
            if (newQuantity <= 0) {
                String prodName = item.getProduct().getProdName();
                items.remove(index);
                recalculateTotal();
                System.out.println(prodName + " has been removed from your cart.");
                return;
            }

            if (item.getProduct().getStock() < newQuantity) {
                System.out.println("Sorry, we only have " + item.getProduct().getStock() + " of this item in stock.");
                return;
            }

            item.setQuantity(newQuantity);
            recalculateTotal();
            System.out.println("Quantity updated successfully.");
        } else {
            System.out.println("Invalid item index.");
        }
    }

    public void clearCart() {
        items.clear();
        totalAmount = 0.0;
        System.out.println("Your cart has been emptied.");
    }
}

package com.gsports.java.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Scanner;

public class Wishlist {
    private String wishlistId;
    private String userId;
    private List<Product> items;

    public Wishlist() {
        this.wishlistId = generateWishlistId();
        this.items = new ArrayList<>();
    }

    public Wishlist(String userId) {
        this.wishlistId = generateWishlistId();
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    // Setters and getters
    public String getWishlistId() {
        return wishlistId;
    }

    public String getUserId() {
        return userId;
    }

    public List<Product> getItems() {
        return items;
    }

    private String generateWishlistId() {
        return "WISH-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public void addItem(Product product) {
        if (product == null) {
            System.out.println("Sorry, we couldn't find that product.");
            return;
        }

        // Check if product already exists in wishlist
        for (Product item : items) {
            if (item.getProdID().equals(product.getProdID())) {
                System.out.println(product.getProdName() + " is already in your wishlist.");
                return;
            }
        }

        // Add new product to wishlist
        items.add(product);
        System.out.println(product.getProdName() + " has been added to your wishlist.");
    }

    public void removeItem(String productId) {
        String removedProductName = "";

        for (Product item : items) {
            if (item.getProdID().equals(productId)) {
                removedProductName = item.getProdName();
                break;
            }
        }
        items.removeIf(item -> item.getProdID().equals(productId));
        if (!removedProductName.isEmpty()) {
            System.out.println(removedProductName + " has been removed from your wishlist.");
        } else {
            System.out.println("That item wasn't in your wishlist.");
        }
    }

    public String displayWishlist() {
        if (items.isEmpty()) {
            return "┌─────────────────────────────────────────────────┐\n" +
                   "│               Your wishlist is empty            │\n" +
                   "│        Add products to build your wishlist      │\n" +
                   "└─────────────────────────────────────────────────┘";
        }
    
        StringBuilder wishlistDetails = new StringBuilder();
        
        // Header
        wishlistDetails.append("\n┌───────────────────────────────────────────────────────────────────────────┐\n");
        wishlistDetails.append("│                               YOUR WISHLIST                               │\n");
        wishlistDetails.append("├────┬──────────┬─────────────────────────────┬─────────────┬───────────────┤\n");
        wishlistDetails.append("│ #  │ ID       │ Product Name                │ Price       │ Stock Status  │\n");
        wishlistDetails.append("├────┼──────────┼─────────────────────────────┼─────────────┼───────────────┤\n");
    
        // Items
        int index = 1;
        for (Product item : items) {
            String formattedId = String.format("%-8s", item.getProdID());
            
            // Truncate long product names
            String formattedName = item.getProdName();
            if (formattedName.length() > 25) {
                formattedName = formattedName.substring(0, 22) + "...";
            }
            formattedName = String.format("%-27s", formattedName);
            
            String formattedPrice = String.format("RM %-7.2f", item.getSellingPrice());
            
            // Show stock status
            String stockStatus;
            if (item.getStock() > 10) {
                stockStatus = "In Stock";
            } else if (item.getStock() > 0) {
                stockStatus = "Low Stock";
            } else {
                stockStatus = "Out of Stock";
            }
            String formattedStock = String.format("%-13s", stockStatus);
            
            wishlistDetails.append(String.format("│ %-2d │ %s │ %s │ %-11s │ %s │\n",
                    index++, formattedId, formattedName, formattedPrice, formattedStock));
        }
        
        // Footer
        wishlistDetails.append("└────┴──────────┴─────────────────────────────┴─────────────┴───────────────┘\n");
        
        return wishlistDetails.toString();
    }
}

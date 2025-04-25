package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Scanner;

public class Wishlist {
    private String wishlistId;
    private String userId;
    private List<Product> items;

    public Wishlist() {
        this.wishlistId = createUniqueWishlistId();
        this.items = new ArrayList<>();
    }

    public Wishlist(String userId) {
        this.wishlistId = createUniqueWishlistId();
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    private String createUniqueWishlistId() {
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

    public void displayWishlist() {
        if (items.isEmpty()) {
            System.out.println("Your wishlist is empty.");
            return;
        }

        System.out.println("\n===== Your Wishlist =====");
        for (int i = 0; i < items.size(); i++) {
            Product item = items.get(i);
            System.out.println((i + 1) + ". " + item.getProdName() + " - $" + item.getUnitPrice());
        }
        
        // Prompt for actions
        promptWishlistActions();
    }
    
    private void promptWishlistActions() {
        Scanner scanner = MenuUtils.getScanner();
        System.out.println("\n===========================================");
        System.out.println("|         What would you like to do?      |");
        System.out.println("===========================================");
        System.out.println("| 1. Move an item to cart                |");
        System.out.println("| 2. Remove an item from wishlist         |");
        System.out.println("| 3. Return to previous menu             |");
        System.out.println("===========================================");
        System.out.print("Please select an option (1-3): ");


        int choice = MenuUtils.getMenuChoice(1, 3);
        
        if (choice == 3) {
            return;
        }
        
        if (items.isEmpty()) {
            System.out.println("Your wishlist is empty.");
            return;
        }
        
        System.out.print("Enter the item number: ");
        int itemNum = 0;
        try {
            itemNum = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (itemNum < 1 || itemNum > items.size()) {
            System.out.println("Invalid item number.");
            return;
        }
        
        Product selectedProduct = items.get(itemNum - 1);
        
        if (choice == 1) {
            // Move to cart
            System.out.print("Enter quantity: ");
            int quantity = 0;
            try {
                quantity = Integer.parseInt(scanner.nextLine().trim());
                
                // Add this check
                if (quantity <= 0) {
                    System.out.println("Quantity must be greater than 0.");
                    return;
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            
            if (UserMenu.getCurrentUser() != null && UserMenu.getCurrentUser() instanceof Customer) {
                Customer customer = (Customer) UserMenu.getCurrentUser();
                customer.getCart().addItem(selectedProduct, quantity);
                // Optionally remove from wishlist after adding to cart
                // items.remove(itemNum - 1);
                // System.out.println(selectedProduct.getProdName() + " moved from wishlist to cart.");
            }
        } else if (choice == 2) {
            // Remove from wishlist
            removeItem(selectedProduct.getProdID());
        }
    }

    public String getWishlistId() {
        return wishlistId;
    }

    public String getUserId() {
        return userId;
    }

    public List<Product> getItems() {
        return items;
    }
}
package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cart {
    private String cartId;
    private String userId;
    private List<CartItem> items;
    private double totalAmount;

    public Cart() {
        this.cartId = createUniqueCartId();
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public Cart(String userId) {
        this.cartId = createUniqueCartId();
        this.userId = userId;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    private String createUniqueCartId() {
        return "CART-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public void addItem(Product product, int quantity) {
        if (product == null) {
            System.out.println("Sorry, we couldn't find that product.");
            return;
        }

        if (quantity <= 0) {
            System.out.println("Please select at least one item to add to your cart.");
            return;
        }

        if (product.getStock() < quantity) {
            System.out.println("Sorry, we only have " + product.getStock() + " of this item in stock.");
            return;
        }

        // Check if product already exists in cart
        for (CartItem item : items) {
            if (item.getProduct().getProdID().equals(product.getProdID())) {
                // Update quantity if product already in cart
                item.setQuantity(item.getQuantity() + quantity);
                recalculateTotal();
                System.out.println("Added more " + product.getProdName() + " to your cart!");
                return;
            }
        }

        // Add new item to cart
        CartItem newItem = new CartItem(product, quantity);
        items.add(newItem);
        recalculateTotal();
        System.out.println(product.getProdName() + " has been added to your cart!");
    }

    public void removeItem(String productId) {
        String removedProductName = "";
        for (CartItem item : items) {
            if (item.getProduct().getProdID().equals(productId)) {
                removedProductName = item.getProduct().getProdName();
                break;
            }
        }
        
        items.removeIf(item -> item.getProduct().getProdID().equals(productId));
        recalculateTotal();
        
        if (!removedProductName.isEmpty()) {
            System.out.println(removedProductName + " has been removed from your cart.");
        } else {
            System.out.println("That item wasn't in your cart.");
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
                    System.out.println("Sorry, we only have " + item.getProduct().getStock() + " of this item in stock.");
                    return;
                }
                item.setQuantity(newQuantity);
                recalculateTotal();
                System.out.println("Updated " + item.getProduct().getProdName() + " quantity to " + newQuantity);
                return;
            }
        }
        System.out.println("That item isn't in your cart yet.");
    }

    public void clearCart() {
        items.clear();
        totalAmount = 0.0;
        System.out.println("Your cart has been emptied.");
    }

    public void updateItemQuantity(int index, int newQuantity) {
        if (index >= 0 && index < items.size()) {
            CartItem item = items.get(index);
            item.setQuantity(newQuantity);
            recalculateTotal();
            System.out.println("Quantity updated successfully.");
        } else {
            System.out.println("Invalid item index.");
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

    private void recalculateTotal() {
        totalAmount = 0.0;
        for (CartItem item : items) {
            totalAmount += item.getSubtotal();
        }
    }

    public void displayCart() {
        if (items.isEmpty()) {
            System.out.println("Your shopping cart is empty. Let's find something you'll love!");
            return;
        }

        System.out.println("\n+-------------------------- YOUR SHOPPING CART --------------------------+");
        System.out.printf("| %-25s %-12s %-12s %-12s |\n", "Product", "Price", "Quantity", "Subtotal");
        System.out.println("+--------------------------------------------------------------------------+");
        
        for (CartItem item : items) {
            System.out.printf("%-25s $%-9.2f %-10d $%-9.2f\n", 
                item.getProduct().getProdName(),
                item.getProduct().getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal());
        }

        System.out.println("\n+------------------------------------------------------+");
        System.out.println("|                    SHOPPING CART                     |");
        System.out.println("+------------------------------------------------------+");
        System.out.printf("|  CART TOTAL: $%-40.2f |\n", totalAmount);
        System.out.println("+------------------------------------------------------+");
        System.out.println("|  1. Proceed to Checkout                              |");
        System.out.println("|  2. Continue Shopping                                |");
        System.out.println("|  3. Update Cart                                      |");
        System.out.println("+------------------------------------------------------+");
        System.out.print("\nPlease select an option (1-3): ");
        
        int choice = MenuUtils.getMenuChoice(1, 3);
        
        if (choice == 1) {
            // Call checkout method from UserMenu
            UserMenu.getInstance().checkout();
        } else if (choice == 3) {
            UserMenu.getInstance().manageCartMenu();
        }
    }

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
}
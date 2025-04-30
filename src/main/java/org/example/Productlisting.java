package org.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Productlisting {
    private Product[] products;

    public Productlisting(Product[] products) {
        this.products = products;
    }

    public void displayProducts() {
        if (products.length == 0) {
            System.out.println("No products available.");
        } else {
            System.out.println("Available Products: ");
            for (int i = 0; i < products.length; i++) {
                System.out.println((i + 1) + ". " + products[i].getDetails());
                System.out.println();
            }
            
            // After displaying products, prompt user for actions
            promptProductActions();
        }
    }
    
    private void promptProductActions() {
        Scanner scanner = MenuUtils.getScanner();
        System.out.println("\n===========================================");
        System.out.println("|         What would you like to do?      |");
        System.out.println("===========================================");
        System.out.println("| 1. Add a product to cart               |");
        System.out.println("| 2. Add a product to wishlist           |");
        System.out.println("| 3. Return to previous menu             |");
        System.out.println("===========================================");

        int choice = MenuUtils.getMenuChoice(1, 3);
        
        if (choice == 3) {
            return;
        }
        
        System.out.print("Enter the product number you want to select: ");
        int productNum = 0;
        try {
            productNum = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (productNum < 1 || productNum > products.length) {
            System.out.println("Invalid product number.");
            return;
        }
        
        Product selectedProduct = products[productNum - 1];
        
        if (choice == 1) {
            // Add to cart
            System.out.print("Enter quantity: ");
            int quantity = 0;
            try {
                quantity = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            
            if (UserMenu.getCurrentUser() != null && UserMenu.getCurrentUser() instanceof Customer) {
                Customer customer = (Customer) UserMenu.getCurrentUser();
                customer.getCart().addItem(selectedProduct, quantity);
            } else {
                System.out.println("You need to be logged in as a customer to add items to cart.");
            }
        } else if (choice == 2) {
            // Add to wishlist
            if (UserMenu.getCurrentUser() != null && UserMenu.getCurrentUser() instanceof Customer) {
                Customer customer = (Customer) UserMenu.getCurrentUser();
                customer.getWishlist().addItem(selectedProduct);
                System.out.println(selectedProduct.getProdName() + " added to your wishlist.");
            } else {
                System.out.println("You need to be logged in as a customer to add items to wishlist.");
            }
        }
    }

    public Product[] searchByName(String name) {
        return Arrays.stream(products)
                .filter(p -> p.getProdName().equalsIgnoreCase(name))
                .toArray(Product[]::new);
    }

    public Product[] searchByCategory(String categoryName) {
        return Arrays.stream(products)
                .filter(p -> p.getCategory().getCategoryName().equalsIgnoreCase(categoryName))
                .toArray(Product[]::new);
    }

    public void sortProductsByPrice() {
        Arrays.sort(products, Comparator.comparing(Product::getUnitPrice));
        System.out.println("Products sorted by price.");
    }
}
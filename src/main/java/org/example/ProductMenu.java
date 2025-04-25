package org.example;

import java.util.Scanner;

// Move this into UserMenu (all menu under one class)

public class ProductMenu {
    Scanner s = new Scanner(System.in);

    Category laptops = new Category("C01", "Laptops", "Laptops Specs");
    Category mouses = new Category("C02", "Mice", "Mice Specs");
    Category accessories = new Category("C03", "Accessories", "Chargers");

    Product[] products = {
            new Laptop("P01", "ROG Gaming Laptop", "High-Performance Laptop", laptops, 7000.0, 10, 
                      "Intel i9", "RTX 4080", 32, 1000, "17.3 inch", "Windows 11"),
            new Mouse("P02", "Logitech G Pro", "Latest Gaming Mouse", mouses, 1000.0, 20,
                     25000, true, 8, "Wireless", "Black"),
            new Accessory("P03", "Fast Charger", "Super Fast Charging Speed", accessories, 100.0, 40,
                         "All laptops", "Charger", "Plastic", "Black")
    };

    public void displayMenu() {
        Productlisting productListing = new Productlisting(products);
        int choice;
        do {
            System.out.println("\n===========================================");
            System.out.println("|            Products Menu               |");
            System.out.println("===========================================");
            System.out.println("| 1. Display All Products               |");
            System.out.println("| 2. Search Product by Name             |");
            System.out.println("| 3. Search Product by Category         |");
            System.out.println("| 4. Sort Products by Price             |");
            System.out.println("| 5. Back                               |");
            System.out.println("===========================================");
            System.out.print("Please enter your choice (1-5): ");

            choice = MenuUtils.getMenuChoice(1, 5);
    
            switch (choice) {
                case 1:
                    productListing.displayProducts();
                    break;
    
                case 2:
                    System.out.print("Enter Product Name to Search: ");
                    String searchName = s.nextLine();
                    Product[] foundByName = productListing.searchByName(searchName);
                    for (Product p : foundByName) {
                        System.out.println(p.getDetails());
                    }
                    break;
    
                case 3:
                    System.out.print("Enter Product Category to Search: ");
                    String searchCategory = s.nextLine();
                    Product[] foundByCategory = productListing.searchByCategory(searchCategory);
                    for (Product p : foundByCategory) {
                        System.out.println(p.getDetails());
                    }
                    break;
    
                case 4:
                    productListing.sortProductsByPrice();
                    break;
    
                case 5:
                    System.out.println("Exiting program...");
                    return;
    
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 5);
    }
}


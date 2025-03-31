package org.example;

import java.util.Scanner;
public class ProductMenu {
    Scanner s = new Scanner(System.in);

    Category laptops = new Category("C01", "Laptops", "Laptops Specs");
    Category mouses = new Category("C02", "Mouses", "Mouses Specs");
    Category accessories = new Category("C03", "Accessories", "Chargers");

    Product[] products = {
            new Product("P01", "ROG NIGGA", "High-Performance Laptop", laptops, 7000.0, 10),
            new Product("P02", "Logitech NIGGA Series", "Latest Black Mouse", mouses, 1000.0, 20),
            new Product("P03", "NIGGA Charger", "SuperIdol Nigga Charge Speed", accessories, 100.0, 40)
    };

    
    

    public void displayMenu() {
        Productlisting productListing = new Productlisting(products);
        int choice;
        do {
            System.out.println("\nProducts Menu:");
            System.out.println("1. Display Products");
            System.out.println("2. Search Product by Name");
            System.out.println("3. Search Product by Category");
            System.out.println("4. Sort Products by Price");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
    
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
                    break;
    
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 5);
    }
}


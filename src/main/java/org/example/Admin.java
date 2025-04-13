package org.example;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class Admin extends User {
    private String accessLevel;

    public Admin(String userID, String username, String email, String password, String accessLevel) {
        super(userID, username, email, password);
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void manageUsers() {
        //
    }

    public void addProduct(Map<String, Product> products) {
        Scanner scanner = MenuUtils.getScanner();
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();
        
        if (products.containsKey(productId)) {
            System.out.println("Product ID already exists. Please use a different ID.");
            return;
        }
        
        System.out.print("Enter Product Name: ");
        String productName = scanner.nextLine();
        
        System.out.print("Enter Product Description: ");
        String productDescription = scanner.nextLine();
        
        System.out.println("Select Product Category: ");
        System.out.println("1. Laptops");
        System.out.println("2. Mouses");
        System.out.println("3. Accessories");
        int categoryChoice = MenuUtils.getMenuChoice(1, 3);
        
        Category category;
        switch (categoryChoice) {
            case 1:
                category = new Category("C01", "Laptops", "Laptops Specs");
                break;
            case 2:
                category = new Category("C02", "Mouses", "Mouses Specs");
                break;
            case 3:
                category = new Category("C03", "Accessories", "Chargers");
                break;
            default:
                System.out.println("Invalid category choice.");
                return;
        }
        
        System.out.print("Enter Product Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Enter Product Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        
        Product newProduct = new Product(productId, productName, productDescription, category, price, stock);
        products.put(productId, newProduct);
        JsonDataHandler.saveProducts(products);
        
        System.out.println("Product added successfully!");
    }

}

package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

public class UserMenu {
    private Scanner scanner;
    private Map<String, User> customers;
    private Map<String, Admin> admins;
    public static User currentUser = null; 


    public UserMenu(Map<String, User> customers, Map<String, Admin> admins) {
        this.scanner = MenuUtils.getScanner();
        this.customers = customers;
        this.admins = admins;
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n=== Welcome to GSports Retail System ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            
            choice = MenuUtils.getMenuChoice(1, 3);
            switch (choice) {
                case 1:
                    registerCustomer();
                    break;
                case 2:
                    if (login()) {
                        if (currentUser instanceof Admin) {
                            adminMenu();
                        } else {
                            productMenu();
                        }
                    }
                    break;
                case 3:
                    JsonDataHandler.saveCustomers(customers);
                    System.out.println("Exiting...");
                    MenuUtils.closeScanner();
                    break;
                }
        } while (choice != 3);
        System.out.println("Thank you for using GSports Retail System!");
        }
    

    private void registerCustomer() {
        System.out.println("\n=== Customer Registration ===");

        // Collect input
        System.out.print("Enter Full Name: ");
        String customerName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = MenuUtils.maskPassword(scanner);

        System.out.print("Enter Phone Number: ");
        String phoneNum = scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        // Register the customer
        try {
            Customer customer = Customer.register(customerName, email, password, phoneNum, address);
            Map<String, User> newCustomer = new HashMap<>();
            newCustomer.put(customer.getUserID(), customer);

            JsonDataHandler.saveCustomers(newCustomer);
            customers = JsonDataHandler.loadCustomers();

            System.out.println("Registration successful! Your customer ID is " + customer.getUserID());
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private boolean login() {
        System.out.println("\n=== Login ===");

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = MenuUtils.maskPassword(scanner);

        User matchingUser = customers.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);

        // Check if the user is an admin
        if (matchingUser == null) {
            matchingUser = admins.values().stream()
                    .filter(user -> email.equals(user.getEmail()))
                    .findFirst()
                    .orElse(null);
        }

        if (matchingUser != null) {
            if (password.equals(matchingUser.getPassword())) {
                currentUser = matchingUser;
                System.out.println("Login successful! Welcome, " + matchingUser.getUsername() + ".");
                return true;
            } else {
                System.out.println("Login failed! Wrong password.");
            }
        } else {
            System.out.println("Login failed! User not found.");
        }
        return false;
    }

    public void adminMenu() {
        int choice;
        do {
        System.out.println("\n=== Admin Dashboard ===");
        System.out.println("1. View All Orders");
        System.out.println("2. View All Products");
        System.out.println("3. View All Customers");
        System.out.println("4. Manage Products");
        System.out.println("5. Manage Customers");
        System.out.println("6. Manage Orders");
        System.out.println("7. Manage Categories");
        System.out.println("8. Logout");
        System.out.println("9. Exit");
        choice = MenuUtils.getMenuChoice(1, 8);

        switch (choice) {
            case 2:
                viewAllProducts();
                break;
            
            case 4:
                manageProductsMenu();
                break;

            case 7:
                manageCategoriesMenu();
                break;

            case 8:
                currentUser = null;
                System.out.println("Logged out successfully.");
                displayMenu();
                break;

            case 9:
                JsonDataHandler.saveCustomers(customers);
                JsonDataHandler.saveAdmins(admins);
                System.out.println("Exiting...");
                MenuUtils.closeScanner();
                break;
        }
        } while (choice != 8 && choice != 9);
        System.out.println("Thank you for using GSports Retail System!");
    }

    private void manageProductsMenu() {
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        Map<String, Product> products = JsonDataHandler.loadProducts();
        if (products == null || products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("=== Manage Products ===");
        System.out.println("1. Add Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("4. View All Products");
        System.out.println("5. Back");

        int choice = MenuUtils.getMenuChoice(1, 5);

        switch (choice) {
            case 1:
                ((Admin) currentUser).addProduct(products);
                break;

            case 2:
                viewAllProducts();
                ((Admin) currentUser).updateProduct(products);
                break;

            case 3:
                viewAllProducts();
                ((Admin) currentUser).deleteProduct(products);
                break;

            case 4:
                viewAllProducts();
                break;

            case 5:
                System.out.println("Returning to Admin Dashboard...");
                break;
        }   

    }

    private void manageCategoriesMenu() {
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        Map<String, Category> categories = JsonDataHandler.loadCategories();
        if (categories == null || categories.isEmpty()) {
            System.out.println("No Categorys available.");
            return;
        }

        System.out.println("=== Manage Categories ===");
        System.out.println("1. Add Category");
        System.out.println("2. Update Category");
        System.out.println("3. Delete Category");
        System.out.println("4. View All Categories");
        System.out.println("5. Back");

        int choice = MenuUtils.getMenuChoice(1, 5);

        switch (choice) {
            case 1:
                ((Admin) currentUser).addCategory(categories);
                break;

            case 2:
                viewAllCategories();
                ((Admin) currentUser).updateCategory(categories);
                break;

            case 3:
                viewAllCategories();
                ((Admin) currentUser).deleteCategory(categories);
                break;
        }
    }

    public void viewAllProducts() {
        Map<String, Product> products = JsonDataHandler.loadProducts();
        Product[] productsArray = products.values().toArray(new Product[0]);
        Arrays.sort(productsArray, Comparator.comparing(Product::getProdID));
        
        System.out.println("=== Product List ===");
        int index = 1;
        for (Product product : productsArray) {
            System.out.println(index + ". " + product.getProdName());
            index++;
        }
    }

    public void viewAllCategories() {
        Map<String, Category> categories = JsonDataHandler.loadCategories();
        Category[] categoriesArray = categories.values().toArray(new Category[0]);
        Arrays.sort(categoriesArray, Comparator.comparing(Category::getCategoryID));
        System.out.println("=== Category List ===");
        int index = 1;
        for (Category category : categoriesArray) {
            System.out.println(index + ". " + category.getCategoryName());
            index++;
        }
    }

    public void productMenu() {

    }
}
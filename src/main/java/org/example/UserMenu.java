package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserMenu {
    private Scanner scanner;
    private Map<String, User> customers;

    public UserMenu(Map<String, User> customers) {
        this.scanner = MenuUtils.getScanner();
        this.customers = customers;
    }

    public void displayMenu() {
        System.out.println("\n=== Welcome to GSports Retail System ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        
        int choice = MenuUtils.getMenuChoice(1, 3);

        switch (choice) {
        case 1:
            registerCustomer();
            break;
        case 2:
            loginCustomer();
            break;
        case 3:
            JsonDataHandler.saveCustomers(customers);
            System.out.println("Exiting...");
            MenuUtils.closeScanner();
            return;
        }
    }

    private void registerCustomer() {
        System.out.println("\n=== Customer Registration ===");

        // Collect input
        System.out.print("Enter Full Name: ");
        String customerName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = User.maskPassword(scanner);

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

    private void loginCustomer() {
        System.out.println("\n=== Login ===");

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = User.maskPassword(scanner);

        User matchingUser = customers.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);

        if (matchingUser != null) {
            if (password.equals(matchingUser.getPassword())) {
                System.out.println("Login successful! Welcome, " + matchingUser.getUsername() + ".");
                ProductMenu productMenu = new ProductMenu();
                productMenu.displayMenu();
                
            } else {
                System.out.println("Login failed! Wrong password.");
            }
        } else {
            System.out.println("Login failed! User not found.");
        }
    }
}




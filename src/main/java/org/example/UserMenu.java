package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserMenu {
    private Scanner scanner;
    private Map<String, User> customers;

    public UserMenu(Map<String, User> customers) {
        this.scanner = new Scanner(System.in);
        this.customers = customers;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Welcome to GSports Retail System ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerCustomer();
                    JsonDataHandler.saveCustomers(customers);
                    break;
                case 2:
                    loginCustomer();
                    break;
                case 3:
                    JsonDataHandler.saveCustomers(customers);
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
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
            System.out.println("Debug: Map created with key: " + customer.getUserID());
            JsonDataHandler.saveCustomers(newCustomer);
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
            } else {
                System.out.println("Login failed! Wrong password.");
            }
        } else {
            System.out.println("Login failed! User not found.");
        }

        // Check if customer exists
//        if (customers.containsKey(email)) {
//            User user = customers.get(email);
//            if (password.equals(user.getPassword())) {
//                System.out.println("Login successful! Welcome, " + user.getUsername() + ".");
//            } else {
//                System.out.println("Incorrect password. Please try again.");
//            }
//        } else {
//            System.out.println("User ID not found. Please register.");
//        }
    }
}




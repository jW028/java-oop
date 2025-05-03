package com.gsports.java.oop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.gsports.java.oop.Order.OrderStatus;
import com.gsports.java.oop.Payment.PaymentMethod;

public class UserMenu {
    private Scanner scanner;
    private List<User> customers;
    private List<Admin> admins;
    private List<Order> orders;
    private List<Payment> payments;
    public static User currentUser = null;
    // Add this as a static instance variable
    private static UserMenu instance;

    public UserMenu() {
        this.scanner = MenuUtils.getScanner();
        this.customers = JsonDataHandler.getCustomersList();
        this.admins = JsonDataHandler.getAdminsList();
        this.orders = JsonDataHandler.getOrdersList();
        this.payments = JsonDataHandler.getPaymentsList();
        instance = this; // Set the instance in the parameterized constructor too
    }

    // Static method to get the instance
    public static UserMenu getInstance() {
        return instance;
    }

    // Make sure this method exists and is static
    public static User getCurrentUser() {
        return currentUser;
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n============================================");
            System.out.println("|       Welcome to GSports Retail System   |");
            System.out.println("============================================");
            System.out.println("| 1. Register                              |");
            System.out.println("| 2. Login                                 |");
            System.out.println("| 3. AI Product Assistant                  |");
            System.out.println("| 4. Exit                                  |");
            System.out.println("============================================");

            choice = MenuUtils.validateDigit(1, 4);
            switch (choice) {
                case 1:
                    registerCustomer();
                    break;
                case 2:
                    login(); // The login method now handles directing to appropriate menus
                    break;
                case 3:
                    chatbotMenu();
                    break;
                case 4:
                    JsonDataHandler.saveCustomersList(customers);
                    JsonDataHandler.saveAdminsList(admins);
                    JsonDataHandler.saveOrdersList(orders);
                    JsonDataHandler.savePaymentsList(payments);
                    System.out.println("Exiting...");
                    MenuUtils.closeScanner();
                    break;
            }
        } while (choice != 4);
        System.out.println("Thank you for using GSports Retail System!");
    }

    private void registerCustomer() {
        System.out.println("\n=== Customer Registration ===");

        // Collect input
        String customerName = "";
        do {
            System.out.print("Enter Full Name: ");
            customerName = scanner.nextLine();

        } while (!isValidFullName(customerName));

        String email = "";
        boolean validEmail = false;
        do {
            System.out.print("Enter Email: ");
            String currentEmail = scanner.nextLine();
            
            // Validate email format with regex
            if (!currentEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("Invalid email format. Please try again.");
                continue;
            }
            
            // Check if email already exists in customers map
            boolean emailExists = customers.stream()
                    .anyMatch(user -> currentEmail.equalsIgnoreCase(user.getEmail()));
                    
            // Also check in admins map
            boolean adminEmailExists = admins.stream()
                .anyMatch(admin -> currentEmail.equalsIgnoreCase(admin.getEmail()));
                    
            if (emailExists || adminEmailExists) {
                System.out.println("Email already registered. Please use a different email.");
            } else {
                validEmail = true;
                email = currentEmail;
            }
        } while (!validEmail);

        String password = "";
        do {
            System.out.print("Enter password (min 8 chars, must include uppercase, lowercase, digit, and special character): ");
            password = MenuUtils.maskPassword(scanner);
        } while (!isValidPassword(password));

        String phoneNum = "";
        do {
            System.out.print("Enter phone number (10-15 digits): ");
            phoneNum = scanner.nextLine();
        } while (!isValidPhoneNumber(phoneNum));

        String address = "";
        do {
            System.out.print("Enter Address: ");
            address = scanner.nextLine();
        } while (!isValidAddress(address));

        // Register the customer
        try {
            // Create a new customer with a generated ID
            Customer customer = new Customer(customerName, email, password, address, phoneNum);

            // Reload the latest customers data to avoid overwriting
            List<User> latestCustomers = JsonDataHandler.getCustomersList();

            // Add to the latest customers list
            latestCustomers.add(customer);

            // Save to JSON
            JsonDataHandler.saveCustomersList(latestCustomers);

            // Update our in-memory map
            customers = latestCustomers;

            System.out.println("Registration successful! Your customer ID is " + customer.getUserID());
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return false;
        }

        // Check if the name contains at least two parts (first and last name)
        String[] nameParts = fullName.trim().split("\\s+");
        if (nameParts.length < 2) {
            System.out.println("Please enter your full name (first and last name).");
            return false;
        }

        // Check if name contains only valid characters (letters, spaces, hyphens, apostrophes)
        if (!fullName.matches("^[a-zA-Z\\s\\-']+$")) {
            System.out.println("Name should contain only letters, spaces, hyphens, and apostrophes.");
            return false;
        }

        // Additional validation: check minimum length for each part
        for (String part : nameParts) {
            if (part.length() < 2) {
                System.out.println("Each part of your name should be at least 2 characters long.");
                return false;
            }
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        // Check minimum length
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long");
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            System.out.println("Password must contain at least one uppercase letter");
            return false;
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            System.out.println("Password must contain at least one lowercase letter");
            return false;
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            System.out.println("Password must contain at least one digit");
            return false;
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            System.out.println("Password must contain at least one special character");
            return false;
        }

        return true;
    }

    /**
     * Validates a phone number
     * @param phoneNumber The phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Remove any spaces or common formatting characters
        String cleanedNumber = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");

        // Check if the number consists of 10-15 digits (typical range for international numbers)
        if (!cleanedNumber.matches("\\d{10,15}")) {
            System.out.println("Phone number must contain 10-15 digits only");
            return false;
        }

        return true;
    }

    /**
     * Validates an address
     * @param address The address to validate
     * @return true if address is valid, false otherwise
     */
    private boolean isValidAddress(String address) {
        // Check if address is not empty and has minimum length
        if (address == null || address.trim().length() < 5) {
            System.out.println("Please enter a valid address (minimum 5 characters)");
            return false;
        }

        // Check if address contains at least some basic elements (alphanumeric characters)
        if (!address.matches(".*[a-zA-Z0-9].*")) {
            System.out.println("Address must contain alphanumeric characters");
            return false;
        }

        return true;
    }

    private boolean login() {
        System.out.println("\n=== Login ===");

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = MenuUtils.maskPassword(scanner);

        User matchingUser = customers.stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);

        // Check if the user is an admin
        if (matchingUser == null) {
            matchingUser = admins.stream()
                    .filter(user -> email.equals(user.getEmail()))
                    .findFirst()
                    .orElse(null);
        }

        if (matchingUser != null) {
            if (password.equals(matchingUser.getPassword())) {
                currentUser = matchingUser;
                System.out.println("Login successful! Welcome, " + matchingUser.getUsername() + ".");

                // Load products immediately after successful login
                System.out.println("Loading product data...");
                System.out.println("Product data loaded successfully.");

                // After successful login, direct to appropriate menu
                if (currentUser instanceof Admin) {
                    adminMenu();
                } else {
                    customerHomepage(); // New homepage for customers
                }
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
            System.out.println("\n============================================");
            System.out.println("|             Admin Dashboard              |");
            System.out.println("============================================");
            System.out.println("| 1. Manage Orders                         |");
            System.out.println("| 2. Manage Products                       |");
            System.out.println("| 3. View All Customers                    |");
            System.out.println("| 4. Generate Sales Report                 |");
            System.out.println("| 5. Logout                                |");
            System.out.println("| 6. Exit                                  |");
            System.out.println("============================================");
            choice = MenuUtils.validateDigit(1, 6);

            switch (choice) {
                case 1: 
                    viewAllOrders();
                    break;

                case 2:
                    manageProductsMenu();
                    break;

                case 3:
                    viewAllCustomers();
                    break;

                case 4:
                    generateAndDisplaySalesReport();
                    break;

                case 5:
                    logout();
                    System.out.println("Logged out successfully.");
                    displayMenu();
                    break;

                case 6:
                    JsonDataHandler.saveCustomersList(customers);
                    JsonDataHandler.saveAdminsList(admins);
                    JsonDataHandler.saveOrdersList(orders);
                    JsonDataHandler.savePaymentsList(payments);
                    System.out.println("Exiting...");
                    MenuUtils.closeScanner();
                    System.exit(0); // Terminate the program
                    break;

                default:
                    System.out.println("Feature not implemented yet.");
                    break;
            }
        } while (choice != 5 && choice != 6);
        System.out.println("Thank you for using GSports Retail System!");
    }

    private void generateAndDisplaySalesReport() {
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }
        
        Admin admin = (Admin) currentUser;
        
        System.out.println("\n=====================================");
        System.out.println("|        Sales Report Menu          |");
        System.out.println("=====================================");
        System.out.println("| 1. All-Time Report                |");
        System.out.println("| 2. Current Month Report           |");
        System.out.println("| 3. Custom Date Range              |");
        System.out.println("| 4. Back to Admin Menu             |");
        System.out.println("=====================================");
        
        int choice = MenuUtils.validateDigit(1, 4);
        
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        switch (choice) {
            case 1:
                // All-time report, leave dates null
                break;
            case 2:
                // Current month report
                YearMonth currentMonth = YearMonth.now();
                startDate = currentMonth.atDay(1).atStartOfDay();
                endDate = LocalDateTime.now();
                break;
            case 3:
                // Custom date range
                try {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    
                    System.out.println("Enter start date (yyyy-MM-dd):");
                    String startDateStr = scanner.nextLine().trim();
                    startDate = LocalDate.parse(startDateStr, dateFormatter).atStartOfDay();
                    
                    System.out.println("Enter end date (yyyy-MM-dd):");
                    String endDateStr = scanner.nextLine().trim();
                    endDate = LocalDate.parse(endDateStr, dateFormatter).atTime(23, 59, 59);
                    
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Report canceled.");
                    return;
                }
                break;
            case 4:
                return; // Back to admin menu
        }
        
        List<Product> products = JsonDataHandler.getProductsList();
        // Generate the report
        String report = admin.generateSalesReport(orders, products, startDate, endDate);
        
        // Display the report
        System.out.println(report);
        
        // Option to save report to file
        System.out.println("\nWould you like to save this report to a file? (Y/N)");
        String saveChoice = scanner.nextLine().trim().toUpperCase();
        
        if (saveChoice.equals("Y")) {
            try {
                // Create reports directory if it doesn't exist
                File reportsDir = new File("reports");
                if (!reportsDir.exists()) {
                    reportsDir.mkdir();
                }
                
                // Generate filename with timestamp
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filename = "reports/sales_report_" + timestamp + ".txt";
                
                // Write report to file
                FileWriter writer = new FileWriter(filename);
                writer.write(report);
                writer.close();
                
                System.out.println("Report saved successfully to " + filename);
                
            } catch (IOException e) {
                System.out.println("Error saving report: " + e.getMessage());
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    private void viewAllCustomers() {
        // Check admin privileges
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        if (customers.isEmpty()) {
            System.out.println("No customers found in the system.");
            return;
        }

        List<User> regularCustomers = customers.stream()
                .filter(user -> user instanceof Customer)
                .collect(java.util.stream.Collectors.toList());

        while (true) {
            // Display customer list with formatted table
            displayCustomersTable(regularCustomers);

            // Customer management menu
            System.out.println("\n=====================================");
            System.out.println("|        Customer Management        |");
            System.out.println("=====================================");
            System.out.println("| 1. View Customer Details          |");
            System.out.println("| 2. View Customer Order History    |");
            System.out.println("| 3. Sort by Name                   |");
            System.out.println("| 4. Sort by Most Orders            |");
            System.out.println("| 5. Back to Admin Menu             |");
            System.out.println("=====================================");

            int choice = MenuUtils.validateDigit(1, 5);

            switch (choice) {
                case 1 -> viewCustomerDetails(regularCustomers);
                case 2 -> viewCustomerOrders(regularCustomers);
                case 3 -> {
                    regularCustomers.sort((c1, c2) -> c1.getUsername().compareToIgnoreCase(c2.getUsername()));
                    System.out.println("Customers sorted by namsdfdsfsd.");
                }
                case 4 -> {
                    regularCustomers.sort((c1, c2) -> {
                        long c1Orders = orders.stream()
                                .filter(order -> order.getCustomer().getUserID().equals(c1.getUserID()))
                                .count();
                        long c2Orders = orders.stream()
                                .filter(order -> order.getCustomer().getUserID().equals(c2.getUserID()))
                                .count();
                        return Long.compare(c2Orders, c1Orders); // Descending order
                    });
                    System.out.println("Customers sorted by most orders.");
                }
                case 5 -> {
                    return;
                }
            }
        }
    }

    // Extract display code to a separate method
    private void displayCustomersTable(List<User> regularCustomers) {
        System.out.println("\n┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                              CUSTOMER LISTINGS                               │");
        System.out.println("├────┬───────────────┬─────────────────────────┬────────────────┬──────────────┤");
        System.out.println("│ #  │ Customer ID   │ Name                    │ Email          │ Total Orders │");
        System.out.println("├────┼───────────────┼─────────────────────────┼────────────────┼──────────────┤");
    
        for (int i = 0; i < regularCustomers.size(); i++) {
            Customer customer = (Customer) regularCustomers.get(i);
            
            // Count orders for this customer
            long orderCount = orders.stream()
                    .filter(order -> order.getCustomer().getUserID().equals(customer.getUserID()))
                    .count();
            
            String formattedId = String.format("%-12s", customer.getUserID());
            String formattedName = String.format("%-23s", 
                    customer.getUsername().length() > 21 ? 
                    customer.getUsername().substring(0, 18) + "..." : 
                    customer.getUsername());
            String formattedEmail = String.format("%-14s", 
                    customer.getEmail().length() > 12 ? 
                    customer.getEmail().substring(0, 9) + "..." : 
                    customer.getEmail());
            String formattedOrders = String.format("%-12d", orderCount);
            
            System.out.printf("│ %-2d │ %s │ %s │ %s │ %s │%n", 
                    i + 1, formattedId, formattedName, formattedEmail, formattedOrders);
        }
        
        // Table footer
        System.out.println("└────┴───────────────┴─────────────────────────┴────────────────┴──────────────┘");
    }

    private void viewCustomerDetails(List<User> customers) {
        System.out.println("~ Enter customer number to view details (0 to go back) ~");
        int customerIndex = MenuUtils.validateDigit(0, customers.size()) - 1;
        if (customerIndex < 0){
            return;
        }
        Customer customer = (Customer) customers.get(customerIndex);
        
        // Calculate total spent
        double totalSpent = orders.stream()
                .filter(order -> order.getCustomer().getUserID().equals(customer.getUserID()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        
        // Count total orders
        long orderCount = orders.stream()
                .filter(order -> order.getCustomer().getUserID().equals(customer.getUserID()))
                .count();
        
        // Display detailed information about the customer
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                             CUSTOMER DETAILS                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Customer ID: " + String.format("%-63s", customer.getUserID()) + " │");
        System.out.println("│ Name: " + String.format("%-69s", customer.getUsername()) + " │");
        System.out.println("│ Email: " + String.format("%-68s", customer.getEmail()) + " │");
        System.out.println("│ Phone: " + String.format("%-68s", customer.getPhoneNum()) + " │");
        System.out.println("│ Address: " + String.format("%-66s", customer.getAddress()) + " │");
        System.out.println("│ Total Orders: " + String.format("%-62d", orderCount) + " │");
        System.out.println("│ Total Spent: RM" + String.format("%-61.2f", totalSpent) + " │");
        
        // Cart items if any
        if (customer.getCart() != null && !customer.getCart().getItems(

        ).isEmpty()) {
            System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
            System.out.println("│                              CURRENT CART                                    │");
            System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
            
            List<CartItem> cartItems = customer.getCart().getItems();
            for (CartItem item : cartItems) {
                System.out.println("│ • " + String.format("%-30s", item.getProduct().getProdName()) + 
                        " | Qty: " + String.format("%-3d", item.getQuantity()) + 
                        " | Price: RM" + String.format("%-8.2f", item.getProduct().getSellingPrice()) + 
                        " | Subtotal: RM" + String.format("%-8.2f", item.getSubtotal()) + " │");
            }
            
            double taxAmount = customer.getCart().getTotalAmount() * Order.TAX_RATE; 
            System.out.println("│ Cart Total: RM" + String.format("%-61.2f", (customer.getCart().getTotalAmount() + taxAmount)) + " │");
        }
        
        // Wishlist items if any
        if (customer.getWishlist() != null && !customer.getWishlist().getItems().isEmpty()) {
            System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
            System.out.println("│                             CURRENT WISHLIST                                │");
            System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
            
            List<Product> wishlistItems = customer.getWishlist().getItems();
            for (Product item : wishlistItems) {
                System.out.println("│ • " + String.format("%-68s", item.getProdName()) + " │");
            }
        }
        
        System.out.println("└─────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewCustomerOrders(List<User> customers) {
        System.out.print("Enter customer number to view orders: ");
        int customerIndex = MenuUtils.validateDigit(1, customers.size()) - 1;
        
        Customer customer = (Customer) customers.get(customerIndex);
        
        List<Order> customerOrders = orders.stream()
                .filter(order -> order.getCustomer().getUserID().equals(customer.getUserID()))
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate())) // Newest first
                .collect(java.util.stream.Collectors.toList());
        
        if (customerOrders.isEmpty()) {
            System.out.println("This customer has no orders.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ Customer: " + String.format("%-65s", customer.getUsername()) + " │");
        System.out.println("│ Order History                                                                │");
        System.out.println("├────┬──────────────┬────────────┬──────────────┬───────────────────────────────┤");
        System.out.println("│ #  │ Order ID     │ Date       │ Total Amount │ Status                        │");
        System.out.println("├────┼──────────────┼────────────┼──────────────┼───────────────────────────────┤");
        
        for (int i = 0; i < customerOrders.size(); i++) {
            Order order = customerOrders.get(i);
            String formattedId = String.format("%-12s", order.getOrderId());
            String formattedDate = String.format("%-10s", order.getFormattedOrderDate().substring(0, 10));
            String formattedAmount = String.format("RM %-10.2f", order.getTotalAmount());
            String formattedStatus = String.format("%-27s", order.getStatus());
            
            System.out.printf("│ %-2d │ %s │ %s │ %s │ %s │%n", 
                    i + 1, formattedId, formattedDate, formattedAmount, formattedStatus);
        }
        
        System.out.println("└────┴──────────────┴────────────┴──────────────┴───────────────────────────────┘");
        
        System.out.println("\nEnter order number to view details (0 to go back): ");
        int choice = MenuUtils.validateDigit(0, customerOrders.size());
        
        if (choice > 0) {
            viewOrderDetailsAdmin(customerOrders);
        }
    }

    // admin viewOrders method
    private void viewAllOrders() {
        // Check admin privileges
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        // Load all orders
        List<Order> allOrders = orders;

        if (allOrders.isEmpty()) {
            System.out.println("No orders found in the system.");
            return;
        }

        while (true) {
            // Table header with formatting
            System.out.println("\n┌─────────────────────────────────────────────────────────────────────────────┐");
            System.out.println("│                                 ORDER LISTINGS                              │");
            System.out.println("├────┬──────────────┬─────────────┬────────────┬──────────────┬───────────────┤");
            System.out.println("│ #  │ Order ID     │ Customer    │ Date       │ Total Amount │ Status        │");
            System.out.println("├────┼──────────────┼─────────────┼────────────┼──────────────┼───────────────┤");

            // Format the output for each order
            for (int i = 0; i < allOrders.size(); i++) {
                Order order = allOrders.get(i);

                // Find customer name
                String customerName = "Unknown";
                for (User user : customers) {
                    if (user.getUserID().equals(order.getCustomer().getUserID())) {
                        customerName = user.getUsername();
                        if (customerName.length() > 9) {
                            customerName = customerName.substring(0, 7) + "..";
                        }
                        break;
                    }
                }

                String formattedId = String.format("%-12s", order.getOrderId());
                String formattedCustomer = String.format("%-11s", customerName);
                String formattedDate = String.format("%-10s", order.getFormattedOrderDate().substring(0, 10));
                String formattedAmount = String.format("RM%-10.2f", order.getTotalAmount());
                String formattedStatus = String.format("%-13s", order.getStatus());

                System.out.printf("│ %-2d │ %s │ %s │ %s │ %s │ %s │%n",
                        i + 1, formattedId, formattedCustomer, formattedDate, formattedAmount, formattedStatus);
            }

            // Table footer
            System.out.println("└────┴──────────────┴─────────────┴────────────┴──────────────┴───────────────┘");

            // Order management menu
            System.out.println("\n=====================================");
            System.out.println("|         Order Management          |");
            System.out.println("=====================================");
            System.out.println("| 1. View Order Details             |");
            System.out.println("| 2. Update Order Status            |");
            System.out.println("| 3. Filter by Status               |");
            System.out.println("| 4. Sort by Date (Newest)          |");
            System.out.println("| 5. Sort by Date (Oldest)          |");
            System.out.println("| 6. Back to Admin Menu             |");
            System.out.println("=====================================");

            int choice = MenuUtils.validateDigit(1, 6);

            switch (choice) {
                case 1 -> viewOrderDetailsAdmin(allOrders);
                case 2 -> updateOrderStatus(allOrders);
                case 3 -> {
                    allOrders = filterOrdersByStatus();
                    continue;
                }
                case 4 -> {
                    allOrders = JsonDataHandler.getOrdersList();
                    allOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                    System.out.println("Orders sorted by date (newest first).");
                    continue;
                }
                case 5 -> {
                    allOrders = JsonDataHandler.getOrdersList();
                    allOrders.sort((o1, o2) -> o1.getOrderDate().compareTo(o2.getOrderDate()));
                    System.out.println("Orders sorted by date (oldest first).");
                    continue;
                }
                case 6 -> {
                    return;
                }
            }
        }
    }



    private List<Order> filterOrdersByStatus() {
        System.out.println("\n=====================================");
        System.out.println("|         Filter by Status          |");
        System.out.println("=====================================");
        System.out.println("| 1. Pending                        |");
        System.out.println("| 2. Processing                     |");
        System.out.println("| 3. Shipped                        |");
        System.out.println("| 4. Delivered                      |");
        System.out.println("| 5. Cancelled                      |");
        System.out.println("| 6. All Orders                     |");
        System.out.println("=====================================");

        int choice = MenuUtils.validateDigit(1, 6);

        if (choice == 6) {
            return JsonDataHandler.getOrdersList(); // Return all orders
        }

        OrderStatus status;
        switch (choice) {
            case 1 -> status = OrderStatus.PENDING;
            case 2 -> status = OrderStatus.PROCESSING;
            case 3 -> status = OrderStatus.SHIPPED;
            case 4 -> status = OrderStatus.DELIVERED;
            case 5 -> status = OrderStatus.COMPLETED;
            default -> status = OrderStatus.PENDING;
        }

        List<Order> filteredOrders = orders.stream()
                .filter(order -> order.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());

        System.out.println("Showing orders with status: " + status.toString());
        return filteredOrders;
    }

    private void viewOrderDetailsAdmin(List<Order> allOrders) {
        System.out.println("~ Enter order number to view details (0 to go back) ~");
        int orderIndex = MenuUtils.validateDigit(0, allOrders.size()) - 1;

        if (orderIndex < 0) {
            return;
        }

        Order selectedOrder = allOrders.get(orderIndex);

        // Find customer name
        String customerName = "Unknown";
        for (User user : customers) {
            if (user.getUserID().equals(selectedOrder.getCustomer().getUserID())) {
                customerName = user.getUsername();
                break;
            }
        }

        // Display detailed information about the order
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                                 ORDER DETAILS                               │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Order ID: " + String.format("%-65s", selectedOrder.getOrderId()) + " │");
        System.out.println("│ Customer: " + String.format("%-65s", customerName) + " │");
        System.out.println("│ Customer ID: " + String.format("%-62s", selectedOrder.getCustomer().getUserID()) + " │");
        System.out.println("│ Order Date: " + String.format("%-63s", selectedOrder.getFormattedOrderDate()) + " │");
        System.out.println("│ Status: " + String.format("%-67s", selectedOrder.getStatus()) + " │");
        System.out.println("│ Total Amount: RM" + String.format("%-59.2f", selectedOrder.getTotalAmount()) + " │");
        System.out.println("│ Shipping Address: " + String.format("%-57s", selectedOrder.getShippingAddress()) + " │");

        // Payment information
        Payment payment = payments.stream()
                .filter(p -> p.getOrderId().equals(selectedOrder.getOrderId()))
                .findFirst()
                .orElse(null);

        if (payment != null) {
            System.out.println("│ Payment Method: " + String.format("%-61s", payment.getPaymentMethod()) + " │");
            System.out.println("│ Payment Status: " + String.format("%-61s", payment.getPaymentStatus()) + " │");
            System.out.println("│ Payment Date: " + String.format("%-63s", payment.getFormattedPaymentDate()) + " │");
        } else {
            System.out.println("│ Payment: No payment information available                                   │");
        }

        System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│                                  ITEMS                                      │");
        System.out.println("├─────┬──────────────────────────────┬────────────┬────────────┬──────────────┤");
        System.out.printf("│ %-3s │ %-28s │ %-10s │ %-10s │ %-12s │\n", " # ", "Product Name", "Quantity", "Price (RM)", "Subtotal(RM)" );
        System.out.println("├─────┼──────────────────────────────┼────────────┼────────────┼──────────────┤");
        // Display items in order
        List<CartItem> items = selectedOrder.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            System.out.println("│ " + String.format("%-3d", i + 1) + " │ " +
                    String.format("%-28s", item.getProduct().getProdName()) +
                    " │ " + String.format("%-10d", item.getQuantity()) +
                    " │ " + String.format("%-10.2f", item.getProduct().getSellingPrice()) +
                    " │ " + String.format("%-12.2f", item.getSubtotal()) + " │");
        }

        System.out.println("└─────┴──────────────────────────────┴────────────┴────────────┴──────────────┘");

        // Option to update status
        System.out.print("\nWould you like to update the status of this order? (Y to confirm): ");
        String updateChoice = scanner.nextLine().trim().toUpperCase();

        if (updateChoice.equals("Y")) {
            updateSingleOrderStatus(selectedOrder);
        } else {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }

    private void updateOrderStatus(List<Order> allOrders) {
        System.out.println("~ Enter order number to update status (0 to go back) ~");
        int orderIndex = MenuUtils.validateDigit(0, allOrders.size()) - 1;
        if (orderIndex < 0){
            return;
        }
        Order selectedOrder = allOrders.get(orderIndex);

        updateSingleOrderStatus(selectedOrder);
    }

    private void updateSingleOrderStatus(Order order) {
        System.out.println("\nCurrent Status: " + order.getStatus());
        System.out.println("\n=====================================");
        System.out.println("|         Select New Status         |");
        System.out.println("=====================================");
        System.out.println("| 1. Pending                        |");
        System.out.println("| 2. Processing                     |");
        System.out.println("| 3. Shipped                        |");
        System.out.println("| 4. Delivered                      |");
        System.out.println("| 5. Cancelled                      |");
        System.out.println("=====================================");

        int statusChoice = MenuUtils.validateDigit(1, 5);
        OrderStatus newStatus;

        switch (statusChoice) {
            case 1 -> newStatus = OrderStatus.PENDING;
            case 2 -> newStatus = OrderStatus.PROCESSING;
            case 3 -> newStatus = OrderStatus.SHIPPED;
            case 4 -> newStatus = OrderStatus.DELIVERED;
            case 5 -> newStatus = OrderStatus.CANCELLED;
            default -> {
                System.out.println("Invalid choice. Status not updated.");
                return;
            }
        }
    
        // Update status
        order.setStatus(newStatus);

        // Save changes
        JsonDataHandler.saveOrdersList(orders);

        Customer customer = order.getCustomer();
        List<Order> updatedOrderHistory = JsonDataHandler.getOrderHistory(customer);
        customer.setOrderHistory(updatedOrderHistory);
    
        // Save the updated customer list to persist the changes
        JsonDataHandler.saveCustomersList(customers);

        System.out.println("Order status updated successfully to: " + newStatus);
    }
        

    private void manageProductsMenu() {
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        List<Product> products = JsonDataHandler.getProductsList();
        if (products == null || products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("\n=====================================");
        System.out.println("|         Manage Products           |");
        System.out.println("=====================================");
        System.out.println("| 1. Add Product                    |");
        System.out.println("| 2. Update Product                 |");
        System.out.println("| 3. Delete Product                 |");
        System.out.println("| 4. View All Products              |");
        System.out.println("| 5. Back                           |");
        System.out.println("=====================================");

        int choice = MenuUtils.validateDigit(1, 5);

        switch (choice) {
            case 1:
                addProduct(products);
                break;

            case 2:
                viewAllProducts();
                updateProduct(products);
                break;

            case 3:
                viewAllProducts();
                deleteProduct(products);
                break;

            case 4:
                viewAllProducts();
                break;

            case 5:
                System.out.println("Returning to Admin Dashboard...");
                break;
        }
    }

    public void viewAllProducts() {
        // Get all products from the JSON file
        List<Product> products = JsonDataHandler.getProductsList();

        // Debug output to check how many products are being loaded
        System.out.println("Debug: Total products loaded: " + products.size());

        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        // Table header with formatting
        System.out.println("\n┌───────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                               PRODUCT LISTING                             │");
        System.out.println("├────┬──────────┬─────────────────────────────┬───────────┬─────────────────┤");
        System.out.println("│ #  │ ID       │ Product Name                │ Price     │ Type            │");
        System.out.println("├────┼──────────┼─────────────────────────────┼───────────┼─────────────────┤");

        // Format the output for each product
        int index = 1;
        for (Product product : products) {
            String formattedId = String.format("%-8s", product.getProdID());
            String formattedName = String.format("%-27s",
                    (product.getProdName().length() > 25) ?
                    product.getProdName().substring(0, 22) + "..." :
                    product.getProdName());
            String formattedPrice = String.format("RM %-7.2f", product.getSellingPrice());
            String formattedType = String.format("%-15s", product.getProductType());

            System.out.printf("│ %-2d │ %s │ %s │ %s │ %s │%n",
                    index, formattedId, formattedName, formattedPrice, formattedType);

            index++;
        }

        // Table footer
        System.out.println("└────┴──────────┴─────────────────────────────┴───────────┴─────────────────┘");
    }

    private void promptProductActions(Product selectedProduct) {
        // Only prompt actions for customers
        if (!(currentUser instanceof Customer)) {
            return;
        }

        Customer customer = (Customer) currentUser;

        System.out.println("\n===================================");
        System.out.println("|        Product Actions          |");
        System.out.println("===================================");
        System.out.println("| 1. Add to Cart                  |");
        System.out.println("| 2. Add to Wishlist              |");
        System.out.println("| 3. Back                         |");
        System.out.println("===================================");

        int choice = MenuUtils.validateDigit(1, 3);

        switch (choice) {
            case 1 -> {
                // Add to cart
                if (selectedProduct.getStock() <= 0) {
                    System.out.println("Sorry, this product is out of stock.");
                    MenuUtils.waitEnter(scanner);
                    return;
                }
                System.out.print("\n~ Quantity to add (Stock available: " + selectedProduct.getStock() + ") ~\n");
                int quantity = MenuUtils.validateDigit(1, selectedProduct.getStock());

                if (quantity > selectedProduct.getStock()) {
                    System.out.println("Sorry, there are only " + selectedProduct.getStock() + " items available.");
                    MenuUtils.waitEnter(scanner);
                    return;
                }

                customer.getCart().addItem(selectedProduct, quantity);

                System.out.println("Cart total: RM" + String.format("%.2f", customer.getCart().getTotalAmount()));
                JsonDataHandler.saveCustomersList(customers);
                break;
            }

            case 2 -> {
                customer.getWishlist().addItem(selectedProduct);
                JsonDataHandler.saveCustomersList(customers);
                break;
            }

            case 3 -> {
                return;
            }
        }
        MenuUtils.waitEnter(scanner);
    }

    public void productMenu() {
        // Load products from JSON or use sample products if needed
        List<Product> products = JsonDataHandler.getProductsList();

        ProductListing productListing = new ProductListing(products);

        int choice;
        do {
            System.out.println("\n==========================================");
            System.out.println("|             Products Menu              |");
            System.out.println("==========================================");
            System.out.println("| 1. Display All Products                |");
            System.out.println("| 2. Search Product by Name              |");
            System.out.println("| 3. Search Product by Category          |");
            System.out.println("| 4. Sort Products by Price Ascending    |");
            System.out.println("| 5. Sort Products by Price Descending   |");
            System.out.println("| 6. Back                                |");
            System.out.println("==========================================");

            choice = MenuUtils.validateDigit(1, 6);
            int productIndex;

            switch (choice) {
                case 1:
                    viewProducts(JsonDataHandler.getProductsList());
                    System.out.println("Select a product to view details (or 0 to go back): ");
                    productIndex = MenuUtils.validateDigit(0, products.size());
                    if (productIndex > 0) {
                        Product selectedProduct = products.get(productIndex - 1);
                        System.out.println("┌─────────────────────────────────────────────────┐");
                        System.out.println("|                 Product Details                 |");
                        System.out.println("├─────────────────────────────────────────────────┤");
                        System.out.println(selectedProduct.getDetails());
                        System.out.println("└─────────────────────────────────────────────────┘");
                        promptProductActions(selectedProduct);
                    } else {
                        System.out.println("Returning to product menu...");
                    }
                    break;

                case 2:
                    System.out.print("Enter Product Name to Search: ");
                    String searchName = scanner.nextLine();
                    List<Product> foundByName = productListing.searchProductByName(searchName);
                    viewProducts(foundByName);
                    if (foundByName.isEmpty()) {
                        System.out.println("No products found with the name: " + searchName);
                    }
                    System.out.println("Select a product to view details (or 0 to go back): ");
                    productIndex = MenuUtils.validateDigit(0, foundByName.size());
                    if (productIndex > 0) {
                        Product selectedProduct = foundByName.get(productIndex - 1);
                        System.out.println("┌─────────────────────────────────────────────────┐");
                        System.out.println("|                 Product Details                 |");
                        System.out.println("├─────────────────────────────────────────────────┤");
                        System.out.println(selectedProduct.getDetails());
                        System.out.println("└─────────────────────────────────────────────────┘");
                        promptProductActions(selectedProduct);
                    } else {
                        System.out.println("Returning to product menu...");
                    }
                    break;

                case 3:
                    List<Product> foundByCategory = productListing.sortProductsByCategory();
                    viewProducts(foundByCategory);
                    if (foundByCategory.isEmpty()) {
                        System.out.println("No products found in the selected category.");
                    }
                    System.out.println("Select a product to view details (or 0 to go back): ");
                    productIndex = MenuUtils.validateDigit(0, foundByCategory.size());
                    if (productIndex > 0) {
                        Product selectedProduct = foundByCategory.get(productIndex - 1);
                        System.out.println("┌─────────────────────────────────────────────────┐");
                        System.out.println("|                 Product Details                 |");
                        System.out.println("├─────────────────────────────────────────────────┤");
                        System.out.println(selectedProduct.getDetails());
                        System.out.println("└─────────────────────────────────────────────────┘");
                        promptProductActions(selectedProduct);
                    } else {
                        System.out.println("Returning to product menu...");
                    }
                    break;

                case 4:
                    List<Product> priceAsc = productListing.sortProductsByPrice(true);
                    viewProducts(priceAsc);
                    System.out.println("Select a product to view details (or 0 to go back): ");
                    productIndex = MenuUtils.validateDigit(0, priceAsc.size());
                    if (productIndex > 0) {
                        Product selectedProduct = priceAsc.get(productIndex - 1);
                        System.out.println("┌─────────────────────────────────────────────────┐");
                        System.out.println("|                 Product Details                 |");
                        System.out.println("├─────────────────────────────────────────────────┤");
                        System.out.println(selectedProduct.getDetails());
                        System.out.println("└─────────────────────────────────────────────────┘");
                        promptProductActions(selectedProduct);
                    } else {
                        System.out.println("Returning to product menu...");
                    }
                    break;

                case 5:
                    List<Product> priceDesc = productListing.sortProductsByPrice(false);
                    viewProducts(priceDesc);
                    System.out.println("Select a product to view details (or 0 to go back): ");
                    productIndex = MenuUtils.validateDigit(0, priceDesc.size());
                    if (productIndex > 0) {
                        Product selectedProduct = priceDesc.get(productIndex - 1);
                        System.out.println("┌─────────────────────────────────────────────────┐");
                        System.out.println("|                 Product Details                 |");
                        System.out.println("├─────────────────────────────────────────────────┤");
                        System.out.println(selectedProduct.getDetails());
                        System.out.println("└─────────────────────────────────────────────────┘");
                        promptProductActions(selectedProduct);
                    } else {
                        System.out.println("Returning to product menu...");
                    }
                    break;
                    
                case 6:    
                    return;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 5);
    }

    public void viewProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        // Table header with formatting
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                               PRODUCT LISTING                              │");
        System.out.println("├────┬──────────┬─────────────────────────────┬────────────┬─────────────────┤");
        System.out.println("│ #  │ ID       │ Product Name                │ Price      │ Type            │");
        System.out.println("├────┼──────────┼─────────────────────────────┼────────────┼─────────────────┤");
    
        // Format the output for each product
        int index = 1;
        for (Product product : products) {
            String formattedId = String.format("%-8s", product.getProdID());
            String formattedName = String.format("%-27s", 
                    (product.getProdName().length() > 25) ? 
                    product.getProdName().substring(0, 22) + "..." : 
                    product.getProdName());
            String formattedPrice = String.format("RM %-7.2f", product.getSellingPrice());
            String formattedType = String.format("%-15s", product.getProductType());
            
            System.out.printf("│ %-2d │ %s │ %s │ %s │ %s │%n", 
                    index, formattedId, formattedName, formattedPrice, formattedType);
            
            index++;
        }
        
        // Table footer
        System.out.println("└────┴──────────┴─────────────────────────────┴────────────┴─────────────────┘");

    }

    private void customerHomepage() {
        MenuUtils.cls();
        int choice;
        do {
            System.out.println("\n===============================================");
            System.out.println("|         GSports Customer Homepage           |");
            System.out.println("===============================================");
            System.out.println("| 1. Browse Products                          |");
            System.out.println("| 2. View Wishlist                            |");
            System.out.println("| 3. View Cart                                |");
            System.out.println("| 4. Order History                            |");
            System.out.println("| 5. My Profile                               |");
            System.out.println("| 6. Logout                                   |");
            System.out.println("| 7. Exit                                     |");
            System.out.println("===============================================");

            choice = MenuUtils.validateDigit(1, 7);

            switch (choice) {
                case 1:
                    productMenu();
                    break;
                case 2:
                    displayInteractiveWishlist();
                    break;
                case 3:
                    if (currentUser instanceof Customer) {
                        displayCart(((Customer) currentUser).getCart());
                        manageCartMenu();
                    }
                    break;
                case 4:
                    viewOrderHistory();
                    break;
                case 5:
                    viewProfile();
                    break;
                case 6:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    return; // Return to main menu
                case 7:
                    JsonDataHandler.saveCustomersList(customers);
                    System.out.println("Thank you for shopping with GSports!");
                    System.out.println("Exiting...");
                    MenuUtils.closeScanner();
                    System.exit(0); // Terminate the program
                    break;
            }
        } while (choice != 6 && choice != 7);
    }

    public void displayWishlistItem(int index) {
        List<Product> items = ((Customer) currentUser).getWishlist().getItems();
        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid item number.");
            return;
        }

        Product item = items.get(index);

        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│              WISHLIST ITEM DETAILS              │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println(item.getDetails());
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();
    }

    public void displayInteractiveWishlist() {
        Customer customer = (Customer) currentUser;
        List<Product> items = ((Customer) currentUser).getWishlist().getItems();
        if (items.isEmpty()) {
            System.out.println("┌─────────────────────────────────────────────────┐");
            System.out.println("│               Your wishlist is empty            │");
            System.out.println("│        Add products to build your wishlist      │");
            System.out.println("└─────────────────────────────────────────────────┘");
            return;
        }

        System.out.println(customer.getWishlist().displayWishlist());

        while (true) {
            System.out.println();
            System.out.println("Options:");
            System.out.println("1. View item details");
            System.out.println("2. Add item to cart");
            System.out.println("3. Remove item from wishlist");
            System.out.println("0. Back to previous menu");

            System.out.print("\nEnter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                System.out.print("Enter item number to view details: ");
                try {
                    int itemIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    displayWishlistItem(itemIndex);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            } else if (choice == 2) {
                System.out.print("Enter item number to add to cart: ");
                try {
                    int itemIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (itemIndex >= 0 && itemIndex < items.size()) {
                        Product selectedProduct = items.get(itemIndex);
                        System.out.print("Enter quantity: ");
                        int quantity = Integer.parseInt(scanner.nextLine());
                        if (quantity > 0 && quantity <= selectedProduct.getStock()) {
                            customer.getCart().addItem(selectedProduct, quantity);
                        } else {
                            System.out.println("Invalid quantity or insufficient stock.");
                        }
                    } else {
                        System.out.println("Invalid item number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            } else if (choice == 3) {
                System.out.print("Enter item number to remove from wishlist: ");
                try {
                    int itemIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (itemIndex >= 0 && itemIndex < items.size()) {
                        customer.getWishlist().removeItem(items.get(itemIndex).getProdID());
                    } else {
                        System.out.println("Invalid item number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void manageCartMenu() {
        if (!(currentUser instanceof Customer)) {
            System.out.println("Access denied. Customer privileges required.");
            return;
        }

        Customer customer = (Customer) currentUser;
        Cart cart = customer.getCart();

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n======================================");
        System.out.println("|            Manage Cart             |");
        System.out.println("======================================");
        System.out.println("| 1. Update Item Quantity            |");
        System.out.println("| 2. Remove Item                     |");
        System.out.println("| 3. Clear Cart                      |");
        System.out.println("| 4. Proceed to Checkout             |");
        System.out.println("| 5. Back                            |");
        System.out.println("======================================");

        int choice = MenuUtils.validateDigit(1, 5);
        int currentQuantity;

        switch (choice) {
            case 1:
                // Update quantity
                displayCart(cart);
                System.out.print("~ Select item index to update (0 to go back) ~\n");
                int updateIndex = MenuUtils.validateDigit(0, cart.getItems().size()) - 1;
                if (updateIndex < 0) {
                    break;
                }

                CartItem item = cart.getItems().get(updateIndex);
                Product product = item.getProduct();
                currentQuantity = item.getQuantity();
                System.out.println("Current quantity: " + currentQuantity);

                // Check available stock first
                if (product.getStock() <= 0) {
                    System.out.println("Sorry, this product is currently out of stock.");
                    System.out.println("You can keep your current quantity of " + currentQuantity + " or remove the item.");
                    System.out.println("Would you like to remove this item from your cart? (Y/N)");
                    String removeChoice = scanner.nextLine().trim().toUpperCase();
                    
                    if (removeChoice.equals("Y")) {
                        cart.removeItem(updateIndex);
                        System.out.println("Item removed from cart.");
                    }
                    break;
                }
                
                // Provide maximum possible quantity options
                int maxPossibleQuantity = Math.max(currentQuantity, product.getStock());
                System.out.println("\n~ Update Quantity ~");
                System.out.println("(Current: " + currentQuantity + ", Available stock: " + product.getStock() + ")");
                System.out.println("Enter new quantity (1-" + maxPossibleQuantity + ") or 0 to cancel:");
                
                int newQuantity;
                try {
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) {
                        System.out.println("No changes made to quantity.");
                        break;
                    }
                    
                    newQuantity = Integer.parseInt(input);
                    
                    if (newQuantity == 0) {
                        System.out.println("Quantity update cancelled.");
                        break;
                    } else if (newQuantity < 0) {
                        System.out.println("Invalid quantity. No changes made.");
                        break;
                    } else if (newQuantity > product.getStock()) {
                        System.out.println("Warning: Only " + product.getStock() + " items are available in stock.");
                        System.out.println("Would you like to set quantity to maximum available? (Y/N)");
                        String maxChoice = scanner.nextLine().trim().toUpperCase();
                        
                        if (maxChoice.equals("Y")) {
                            cart.updateItemQuantity(updateIndex, product.getStock());
                            System.out.println("Quantity updated to maximum available: " + product.getStock());
                        } else {
                            System.out.println("No changes made to quantity.");
                        }
                    } else {
                        cart.updateItemQuantity(updateIndex, newQuantity);
                        System.out.println("Quantity updated to: " + newQuantity);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. No changes made to quantity.");
                }
                break;
            

            case 2:
                // Remove item
                displayCart(cart);
                System.out.println("~ Enter item index to remove/reduce (0 to go back) ~");
                int removeIndex = MenuUtils.validateDigit(0, cart.getItems().size()) - 1;

                if (removeIndex < 0) {
                    break;
                }

                CartItem itemToModify = cart.getItems().get(removeIndex);
                currentQuantity = itemToModify.getQuantity();

                if (currentQuantity == 1) {
                    System.out.println("You only have 1 item. Remove completely? (Y/N)");
                    if (scanner.nextLine().trim().toUpperCase().equals("Y")) {
                        cart.removeItem(removeIndex);
                        System.out.println("Item removed from cart.");
                    }
                } else {
                    // Ask how many to remove
                    System.out.println("Current quantity: " + currentQuantity);
                    System.out.println("How many would you like to remove?");
                    int removeQuantity = MenuUtils.validateDigit(1, currentQuantity);
                    
                    if (removeQuantity == currentQuantity) {
                        // Remove completely if all units are being removed
                        cart.removeItem(removeIndex);
                        System.out.println("Item removed from cart.");
                    } else {
                        // Update with reduced quantity
                        int updatedQuantity = currentQuantity - removeQuantity;
                        cart.updateItemQuantity(removeIndex, updatedQuantity);
                        System.out.println("Quantity updated to: " + updatedQuantity);
                    }
                }
                break;

            case 3:
                // Clear cart
                System.out.print("Are you sure you want to clear your cart? (Y to confirm): ");
                if (scanner.nextLine().trim().toUpperCase().equals("Y")) {
                    cart.clearCart();
                } else {
                    System.out.println("\nClear cart cancelled. Returning to Homepage...");
                }
                break;

            case 4:
                // Proceed to checkout
                checkout();
                break;

            case 5:
                return;
            }        
        }
   


    private void displayCart(Cart cart) {
        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Your shopping cart is empty. Let's find something you'll love!");
            return;
        }
    
        // Calculate summary values
        double subtotal = cart.getTotalAmount();
        double tax = subtotal * Order.TAX_RATE; // 6% tax rate
        double total = subtotal + tax;
    
        // Adjusted table with proper spacing and column widths
        System.out.println("\n┌───────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                           YOUR SHOPPING CART                          │");
        System.out.println("├────┬───────────────────────────────┬───────────┬─────────┬────────────┤");
        System.out.printf("│ #  │ %-29s │ %-9s │ %-7s │ %-10s │\n", "Product", "Price", "Qty", "Subtotal");
        System.out.println("├────┼───────────────────────────────┼───────────┼─────────┼────────────┤");
        
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            String productName = item.getProduct().getProdName().length() > 27 ? 
                                item.getProduct().getProdName().substring(0, 24) + "..." : 
                                item.getProduct().getProdName();
                                
            System.out.printf("│ %-2d │ %-29s │ RM%-7.2f │ %-7d │ RM%-8.2f │\n",
                    i + 1,                // Item number (1-based)
                    productName,          // Product name (shortened if needed)
                    item.getProduct().getSellingPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
        }
        
        // Footer with proper alignment matching the header
        System.out.println("├────┴───────────────────────────────┴───────────┴─────────┼────────────┤");
        System.out.printf("│ %56s │ RM%-8.2f │\n", "Subtotal:", subtotal);
        System.out.printf("│ %56s │ RM%-8.2f │\n", "Tax (6%):", tax);
        System.out.println("├──────────────────────────────────────────────────────────┼────────────┤");
        System.out.printf("│ %56s │ RM%-8.2f │\n", "TOTAL:", total);
        System.out.println("└──────────────────────────────────────────────────────────┴────────────┘");
    }

    private void logout() {
        JsonDataHandler.saveCustomersList(customers);
        JsonDataHandler.saveAdminsList(admins);
        JsonDataHandler.saveOrdersList(orders);
        JsonDataHandler.savePaymentsList(payments);
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private void viewProfile() {
        System.out.println("\n========== My Profile ==========");
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            String formattedDescription = Product.Utils.wrapText(customer.getAddress(), 28);
            String[] descriptionLines = formattedDescription.split("\n");
            StringBuilder descriptionBuilder = new StringBuilder();
            for (String line : descriptionLines) {
                descriptionBuilder.append(String.format("│ %-28s │\n", line));
            }


            System.out.printf("│ User ID : %-18s │\n", customer.getUserID());
            System.out.printf("│ Name    : %-18s │\n", customer.getUsername());
            System.out.printf("│ Email   : %-18s │\n", customer.getEmail());
            System.out.printf("│ Phone   : %-18s │\n", customer.getPhoneNum());
            System.out.printf("│ Address: %-19s │\n", " ");
            System.out.println(descriptionBuilder.toString().trim());
            System.out.println("└──────────────────────────────┘");

            System.out.println("\nWould you like to edit your profile? (Y/N)");
            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("Y")) {
                editProfile(customer);
            }
        }
    }

    private void editProfile(Customer customer) {
        System.out.println("\n=====================================");
        System.out.println("|           Edit Profile            |");
        System.out.println("=====================================");
        System.out.println("| 1. Edit Name                      |");
        System.out.println("| 2. Edit Address                   |");
        System.out.println("| 3. Edit Phone Number              |");
        System.out.println("| 4. Change Password                |");
        System.out.println("| 5. Back to Profile                |");
        System.out.println("=====================================");

        int choice = MenuUtils.validateDigit(1, 5);

        switch (choice) {
            case 1:
                // Edit name functionality
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                if (isValidFullName(newName)) {
                    customer.setUsername(newName);
                    System.out.println("Name updated successfully!");
                } else {
                    System.out.println("Name update cancelled.");
                }
                break;
            case 2:
                System.out.print("Enter new address: ");
                String newAddress = scanner.nextLine();
                if (isValidAddress(newAddress)) {
                    customer.setAddress(newAddress);
                    System.out.println("Address updated successfully!");
                } else {
                    System.out.println("Address update cancelled.");
                }
                break;
            case 3:
                String newPhone = "";
                boolean validPhone = false;
                do {
                    System.out.print("Enter new phone number (leave empty to cancel): ");
                    newPhone = scanner.nextLine();
                    if (newPhone.isEmpty()) {
                        System.out.println("Phone number update cancelled.");
                        return;
                    }
                    validPhone = isValidPhoneNumber(newPhone);
                } while (!validPhone);
                customer.setPhoneNum(newPhone);
                System.out.println("Phone number updated successfully!");
                break;

            case 4:

                System.out.print("Enter current password: ");
                String currentPassword = MenuUtils.maskPassword(scanner);
                if (currentPassword.isEmpty()) {
                    System.out.println("Password update cancelled.");
                    break;
                }
                if (currentPassword.equals(customer.getPassword())) {
                    boolean validNewPassword = false;
                    String newPassword = "";

                    do {
                        System.out.print("Enter new password: ");
                        newPassword = MenuUtils.maskPassword(scanner);

                        if (newPassword.isEmpty()) {
                            System.out.println("Password update cancelled.");
                            return;
                        }

                        validNewPassword = isValidPassword(newPassword);

                    } while (!validNewPassword);

                    // Confirm password
                    System.out.print("Confirm new password: ");
                    String confirmPassword = MenuUtils.maskPassword(scanner);

                    if (!confirmPassword.equals(newPassword)) {
                        System.out.println("Passwords do not match. Password update cancelled.");
                        break;
                    }
                    customer.setPassword(newPassword);
                    System.out.println("Password updated successfully!");
                } else {
                    System.out.println("Incorrect current password.");
                }
                break;
            case 5:
                return;
        }
    }

    private void viewOrderHistory() {
        System.out.println("\n=== Order History ===");

        if (!(currentUser instanceof Customer)) {
            System.out.println("Access denied. Customer privileges required.");
            return;
        }

        Customer customer = (Customer) currentUser;
        System.out.println("User ID: " + customer.getUserID());

        // Always fetch the latest order history directly from the database
        List<Order> userOrders = JsonDataHandler.getOrderHistory(customer);

        // Update the customer's order history with the latest data
        customer.setOrderHistory(userOrders);

        if (userOrders == null || userOrders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }

        System.out.println("========================================");
        System.out.println("│          Your Order History          │");
        System.out.println("========================================");

        // Display orders with their current status
        for (int i = 0; i < userOrders.size(); i++) {
            Order order = userOrders.get(i);
            System.out.printf("│ Order #%-29d │\n",(i + 1));
            System.out.printf("│ Order ID    : %-22s │\n", order.getOrderId());
            System.out.printf("│ Date        : %-22s │\n", order.getFormattedOrderDate());
            System.out.printf("│ Total       : RM%-20.2f │\n", order.getTotalAmount());
            System.out.printf("│ Status      : %-22s │\n", order.getStatus());
            System.out.printf("│ Last Updated: %-22s │\n", order.getFormattedLastUpdated());
            System.out.printf("├--------------------------------------┤\n");
        }

        System.out.println("\n~ Enter order number to view details (0 to go back) ~");
        int choice = MenuUtils.validateDigit(0, userOrders.size());

        if (choice > 0) {
            displayOrderDetails(userOrders.get(choice - 1));
        }

        System.out.println("Returning to Homepage...");
    }

    public void checkout() {
        if (!(currentUser instanceof Customer)) {
            System.out.println("Access denied. Customer privileges required.");
            return;
        }

        Customer customer = (Customer) currentUser;
        Cart cart = customer.getCart();

        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty. Add some products before checkout.");
            return;
        }

        System.out.println("\n=====================================");
        System.out.println("|             Checkout              |");
        System.out.println("=====================================");
        double subtotal = cart.getTotalAmount();
        double tax = subtotal * Order.TAX_RATE; // 6% tax
        double total = subtotal + tax;
        System.out.println("│ Subtotal: RM" + String.format("%-21.2f", subtotal) + " │");
        System.out.println("│ Tax (6%): RM" + String.format("%-21.2f", tax) + " │");
        System.out.println("│ Total   : RM" + String.format("%-21.2f", total) + " │");
        System.out.println("=====================================");

// Confirm shipping address
        System.out.println("\nCurrent shipping address: " + customer.getAddress());
        System.out.println("=====================================");

        System.out.print("\nWould you like to use this address? (Y/N): ");
        String addressChoice = scanner.nextLine().trim().toUpperCase();

        String shippingAddress;
        if (addressChoice.equals("Y")) {
            shippingAddress = customer.getAddress();
        } else if (addressChoice.toLowerCase().equals("N")) {
            System.out.println("Enter new shipping address:");
            shippingAddress = scanner.nextLine().trim();
        } else {
            System.out.println("Invalid choice. Using current address.");
            shippingAddress = customer.getAddress();
        }

        // Select payment method
        System.out.println("\n=======================================");
        System.out.println("|        Select Payment Method        |");
        System.out.println("=======================================");
        System.out.println("│ 1. Credit Card                      │");
        System.out.println("│ 2. PayPal                           │");
        System.out.println("│ 3. Bank Transfer                    │");
        System.out.println("=======================================");

        int paymentChoice = MenuUtils.validateDigit(1, 3);
        Payment.PaymentMethod paymentMethod;

        switch (paymentChoice) {
            case 1:
                paymentMethod = PaymentMethod.CREDIT_CARD;
                break;
            case 2:
                paymentMethod = PaymentMethod.PAYPAL;
                break;
            case 3:
                paymentMethod = PaymentMethod.BANK_TRANSFER;
                break;
            default:
                paymentMethod = PaymentMethod.CREDIT_CARD;
        }

        System.out.println("\nSelected Payment Method: " + paymentMethod);

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // Process payment
        Payment payment = new Payment(
                orderId,
                cart.getTotalAmount(),
                paymentMethod,
                currentUser
        );

        boolean otpSent = payment.processPayment();

        if (otpSent) {
            final int MAX_ATTEMPTS = 3;
            int attempts = 0;
            boolean paymentSuccess = false;

            while (attempts < MAX_ATTEMPTS && !paymentSuccess) {
                attempts++;
                System.out.print("Enter OTP sent to your email: ");
                String enteredOtp = scanner.nextLine().trim();

                paymentSuccess = payment.verifyAndCompletePayment(enteredOtp);

                if (paymentSuccess) {
                    System.out.println("Payment completed!");
                    // Proceed with order confirmation, etc.
                } else if (attempts < MAX_ATTEMPTS) {
                    System.out.println("Invalid OTP. Please try again.");
                } else {
                    System.out.println("Maximum attempts reached. Payment has been canceled.");
                    // Perhaps offer to restart the payment process
                }
            }

            if (paymentSuccess) {
                // Create order
                Order order = new Order(
                        orderId,
                        customer,
                        new ArrayList<>(cart.getItems()),
                        cart.getTotalAmount(),
                        shippingAddress,
                        payment
                );

                // Set order status to PENDING
                order.setStatus(Order.OrderStatus.PENDING);

                // Schedule status update to PAID after 15 minutes
                order.scheduleStatusUpdate(Order.OrderStatus.PROCESSING, 15);

                // Add order to customer history
                customer.addOrder(order);

                // Save payment
                payments.add(payment);
                JsonDataHandler.savePaymentsList(payments);

                // Save order
                orders.add(order);
                JsonDataHandler.saveOrdersList(orders);

                // Update product stock
                List<Product> allProducts = JsonDataHandler.getProductsList();
                for (CartItem item : cart.getItems()) {
                    Product product = item.getProduct();
                    int newStock = product.getStock() - item.getQuantity();
                    product.setStock(newStock);
                    allProducts.add(product); // Update only the ordered products
                }

            // Save all products back to the database
            JsonDataHandler.saveProductsList(allProducts);

                // Clear the cart
                cart.clearCart();
                JsonDataHandler.saveCustomersList(customers);


                System.out.println("\nOrder placed successfully!");
            } else {
                System.out.println("\nOrder was not completed due to payment failure.");
            }
        }
    }

    public void chatbotMenu() {
        try {
            GeminiService gemini = GeminiService.getInstance();

            System.out.println("GBot is ready! Type 'exit' to quit.");
            System.out.printf("\nHi! How can I help you today?\n");
            System.out.println("You can ask about our products, specifications, pricing, or anything else you need help with.");

            String userInput;

            // Main conversation loop
            boolean conversationFlag = true;
            while (conversationFlag) {
                System.out.print("\nYou: ");
                userInput = scanner.nextLine().trim();

                // Check for exit command
                if (userInput.equalsIgnoreCase("exit")
                        || userInput.equalsIgnoreCase("quit")
                        || userInput.equalsIgnoreCase("bye")) {
                    System.out.println("\nThank you for using the GSports Assistant. Returning to main menu...");
                    conversationFlag = false;
                    return;
                }

                // Skip empty inputs
                if (userInput.isEmpty()) {
                    continue;
                }

                // Get response from the chatbot
                System.out.println("\nAssistant: Processing your request...");
                String response = gemini.processQuery(userInput);

                // Display the response with proper formatting
                System.out.print("\nGBot: ");
                System.out.println(response);
            }
        } catch (Exception e) {
            System.err.println("\nError: There was a problem with the assistant.");
            System.err.println("Details: " + e.getMessage());
            MenuUtils.waitEnter(scanner);
        }
    }



        // Start the timer thread
        private void displayOrderDetails(Order order) {
            // Create a flag to control the timer thread
            final boolean[] keepRunning = {true};
            Thread timerThread = null;
        
            try {
                // Check if refund window has expired and update status if needed
                // Do this BEFORE displaying order details to ensure correct status is shown
                long initialRefundTime = order.getRemainingRefundTime();
                if (initialRefundTime <= 0 && order.getStatus() == Order.OrderStatus.PENDING) {
                    // Update order status to PAID if refund window expires
                    order.setStatus(Order.OrderStatus.PAID);
                    
                    // Update the order in the orders list
                    for (int i = 0; i < orders.size(); i++) {
                        if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                            orders.set(i, order);
                            break;
                        }
                    }
                    
                    // Save changes to JSON
                    JsonDataHandler.saveOrdersList(orders);
                    
                    // Update the customer's order history if applicable
                    if (currentUser instanceof Customer) {
                        Customer customer = (Customer) currentUser;
                        List<Order> updatedOrderHistory = JsonDataHandler.getOrderHistory(customer);
                        customer.setOrderHistory(updatedOrderHistory);
                        JsonDataHandler.saveCustomersList(customers);
                    }
                    
                    System.out.println("Refund window has expired. Order status updated to PAID.");
                }
        
                // Initial display of order details
                System.out.println("\n=====================================");
                System.out.println("|          Order Details            |");
                System.out.println("=====================================");
                System.out.println("| Order ID: " + order.getOrderId());
                System.out.println("| Order Date: " + order.getFormattedOrderDate());
                System.out.println("| Total Amount: RM" + String.format("%.2f", order.getTotalAmount()));
                System.out.println("| Shipping Address: " + order.getShippingAddress());
        
                Payment payment = payments.stream()
                        .filter(p -> p.getOrderId().equals(order.getOrderId()))
                        .findFirst()
                        .orElse(null);
                if (payment != null) {
                    System.out.println("| Payment Method: " + payment.getPaymentMethod());
                    System.out.println("| Payment Status: " + payment.getPaymentStatus());
                } else {
                    System.out.println("| Payment Method: N/A");
                    System.out.println("| Payment Status: N/A");
                }
                System.out.println("| Order Status: " + order.getStatus());
        
                // Display status update timer if pending
                if (order.getStatus() == Order.OrderStatus.PENDING) {
                    long remainingTimeInSeconds = order.getRemainingTimeForStatusUpdate();
                    if (remainingTimeInSeconds > 0) {
                        long minutes = remainingTimeInSeconds / 60;
                        long seconds = remainingTimeInSeconds % 60;
                        System.out.println("| Status will update to PROCESSING in: " +
                                String.format("%02d:%02d", minutes, seconds));
                    } else {
                        System.out.println("| Status update: Pending update to PROCESSING");
                    }
                } else {
                    System.out.println("| Status update: N/A");
                }
        
                // Display refund window timer
                long remainingRefundTime = order.getRemainingRefundTime();
                if (remainingRefundTime > 0 &&
                        (order.getStatus() == Order.OrderStatus.PENDING || order.getStatus() == Order.OrderStatus.DELIVERED) &&
                        order.getStatus() != Order.OrderStatus.CANCEL_REQUESTED)  {
                    // Use 15 minutes directly instead of the private constant
                    LocalDateTime cancelWindowEnd = order.getOrderDate().plusMinutes(15);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String cancelWindowEndTime = cancelWindowEnd.format(formatter);
                    System.out.println("| Cancel window ends at: " + cancelWindowEndTime);
                } else {
                    System.out.println("| Cancel window: Expired or N/A");
                }
                System.out.println("=====================================");
        
                // Display ordered items with relevant details only
                System.out.println("\n--- Ordered Items ---");
                System.out.printf("%-30s %-10s %-10s %-10s\n", "Product", "Price", "Quantity", "Subtotal");
                System.out.println("------------------------------------------------------------------");
        
                for (CartItem item : order.getItems()) {
                    Product product = item.getProduct();
                    System.out.printf("%-30s RM%-9.2f %-10d RM%-9.2f\n",
                            product.getProdName(),
                            product.getSellingPrice(),
                            item.getQuantity(),
                            item.getSubtotal());
                }
        
                System.out.println("-------------------------------------------------2" +
                        "t-----------------");
                System.out.printf("Total: RM%.2f\n", order.getTotalAmount());
        
                // Create a dedicated area for timer updates that won't interfere with input
                System.out.println("\n--- Timer Updates Will Appear Here ---\n");
        
                // Create a thread to update only the timer values
                timerThread = new Thread(() -> {
                    try {
                        // Store previous values to only update when changed
                        long prevStatusTime = -1;
        
                        while (keepRunning[0]) {
                            // Sleep for 1 second before checking again (more responsive)
                            Thread.sleep(1000);
        
                            // Get current timer values
                            long statusTime = order.getRemainingTimeForStatusUpdate();
        
                            // Only update if values have changed
                            if (statusTime != prevStatusTime) {
                                prevStatusTime = statusTime;
        
                                // Create a string builder for the update message
                                StringBuilder updateMsg = new StringBuilder();
        
                                if (order.getStatus() == Order.OrderStatus.PENDING && statusTime > 0) {
                                    long minutes = statusTime / 60;
                                    long seconds = statusTime % 60;
                                    updateMsg.append("Status will update in ")
                                            .append(String.format("%02d:%02d", minutes, seconds));
                                }
        
                                // Clear the line and print the update if there's anything to show
                                if (updateMsg.length() > 0) {
                                    System.out.print("\r" + " ".repeat(80) + "\r"); // Clear the line
                                    System.out.print(updateMsg.toString());
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        // Thread was interrupted, exit gracefully
                    }
                });
        
                // Start the timer thread
                timerThread.start();
        
                // Show different options based on order status - AFTER starting the timer thread
                if (order.getStatus() == Order.OrderStatus.DELIVERED) {
                    System.out.println("\n1. Finalize Order (Confirm Receipt)");
                    System.out.println("2. Request Refund");
                    System.out.println("0. Back");
                    
                    int choice = MenuUtils.validateDigit(0, 2);
                    if (choice == 1) {
                        // Stop the timer thread before showing finalize dialog
                        keepRunning[0] = false;
                        try {
                            timerThread.join();
                            finalizeOrder(order);
                        } catch (InterruptedException e) {
                            System.err.println("Thread interruption occurred: " + e.getMessage());
                            finalizeOrder(order);
                        }
                    } else if (choice == 2) {
                        // Stop the timer thread before showing refund dialog
                        keepRunning[0] = false;
                        try {
                            timerThread.join();
                            requestRefund(order, payment);
                        } catch (InterruptedException e) {
                            System.err.println("Thread interruption occurred: " + e.getMessage());
                            requestRefund(order, payment);
                        }
                    }
                } else if ((order.getStatus() == Order.OrderStatus.PENDING || 
                        order.getStatus() == Order.OrderStatus.DELIVERED) &&
                        remainingRefundTime > 0 &&
                        order.getStatus() != Order.OrderStatus.CANCEL_REQUESTED ) {
                    System.out.println("\n1. Request Cancel");
                    System.out.println("0. Back");
                    
                    int choice = MenuUtils.validateDigit(0, 1);
                    if (choice == 1) {
                        // Stop the timer thread before showing refund dialog
                        keepRunning[0] = false;
                        try {
                            timerThread.join();
                            requestRefund(order, payment);
                        } catch (InterruptedException e) {
                            System.err.println("Thread interruption occurred: " + e.getMessage());
                            requestRefund(order, payment);
                        }
                    }
                } else {
                    System.out.println("\nPress Enter to go back...");
                    scanner.nextLine();
                }
            } finally {
                // Ensure the timer thread is stopped when we exit
                keepRunning[0] = false;
                if (timerThread != null && timerThread.isAlive()) {
                    try {
                        timerThread.join(1000); // Wait up to 1 second for thread to finish
                    } catch (InterruptedException e) {
                        // Ignore interruption during cleanup
                    }
                }
            }
        }

// Add a new method to handle finalizing an order
private void finalizeOrder(Order order) {
    System.out.println("\n=====================================");
    System.out.println("|          Finalize Order           |");
    System.out.println("=====================================");

    System.out.println("Are you sure you want to finalize this order? (Y/N)");
    System.out.println("This confirms you have received all items in good condition.");

    String choice = scanner.nextLine().trim().toUpperCase();

    if (choice.equals("Y")) {
        // Update order status to COMPLETED
        order.setStatus(Order.OrderStatus.COMPLETED);

        // Save changes
        JsonDataHandler.saveOrdersList(orders);

        System.out.println("Order has been finalized successfully!");
        System.out.println("Thank you for shopping with GSports!");
    } else {
        System.out.println("Order finalization cancelled.");
    }

    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
}


private void requestRefund(Order order, Payment payment) {
    System.out.println("\n=====================================");
    System.out.println("|       Request Cancellation         |");
    System.out.println("=====================================");
    long remainingRefundTime = order.getRemainingRefundTime();
    
    // Check if refund is allowed based on time only
    if (remainingRefundTime <= 0) {
        System.out.println("Sorry, the refund window has expired. Refunds are only available");
        System.out.println("within 15 minutes of placing your order.");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        return;
    }

    System.out.println("Are you sure you want to request for a cancellation? (Y/N)");
    LocalDateTime cancelWindowEnd = order.getOrderDate().plusMinutes(15);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String cancelWindowEndTime = cancelWindowEnd.format(formatter);
    System.out.println("Cancel window ends at: " + cancelWindowEndTime);

    String choice = scanner.nextLine().trim().toUpperCase();

    if (choice.equals("Y")) {
        // Update order status to CANCEL_REQUESTED
        order.updateStatus(Order.OrderStatus.CANCEL_REQUESTED);

        // Update payment status to CANCEL_REQUESTED
        if (payment != null) {
            payment.setPaymentStatus(Payment.PaymentStatus.CANCEL_REQUESTED);
        }

        // Find and update the order in the global orders list
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                orders.set(i, order);
                break;
            }
        }

        // Save changes
        JsonDataHandler.saveOrdersList(orders);
        JsonDataHandler.savePaymentsList(payments);

        // Update the customer's order history to reflect the change
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            List<Order> updatedOrderHistory = JsonDataHandler.getOrderHistory(customer);
            customer.setOrderHistory(updatedOrderHistory);
            JsonDataHandler.saveCustomersList(customers);
        }

        System.out.println("Cancellation requested successfully. Your order status is now CANCELLED.");
        System.out.println("The refund will be processed within 3-5 business days.");
    } else {
        System.out.println("Cancellation request cancelled.");
    }

    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
}

    public void addProduct(List<Product> products) {
        // Check if user is admin
        if (!(currentUser instanceof Admin)) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        try {

            System.out.println("\n=========== Add New Product ===========");
            System.out.println("Select Product Type:");
            System.out.println("1. Laptop");
            System.out.println("2. Mouse");
            System.out.println("3. Accessory");
            System.out.println("4. Back");

            int choice = MenuUtils.validateDigit(1, 4);
            if (choice == 4) {
                return; // Go back to the previous menu
            }

            Scanner scanner = MenuUtils.getScanner();
            // Auto-generate product ID by incrementing from the last one
            String productId;
            if (products.isEmpty()) {
                productId = "P001"; // Starting ID if no products exist
            } else {
                // Get the highest existing product ID and increment it
                int lastId = products.stream()
                    .map(product -> product.getProdID())
                    .filter(id -> id.startsWith("P"))
                    .map(id -> id.substring(1))
                    .mapToInt(numStr -> {
                    try { return Integer.parseInt(numStr); }
                    catch (NumberFormatException e) { return 0; }
                    })
                    .max()
                    .orElse(0);
                productId = "P" + String.format("%03d", lastId + 1);
            }
            System.out.println("Generated Product ID: " + productId);
            
            if (products.contains(productId)) {
                System.out.println("Product ID already exists. Please use a different ID.");
                return;
            }
            
            // Common product attributes
            System.out.print("Enter Product Name: ");
            String productName = scanner.nextLine();
            
            System.out.print("Enter Product Description: ");
            String productDescription = scanner.nextLine();
            
            System.out.print("Enter Product Unit Price: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter Product Selling Price: ");
            double sellingPrice = Double.parseDouble(scanner.nextLine());
            int stock = MenuUtils.validateDigit("Enter Product Stock ", 0, 1000);
            
            Product newProduct = null;

            // Create specific product type 
            switch (choice) {
                case 1 -> {
                    System.out.println("\n=== Enter Laptop Specific Details ===");
                    System.out.print("Processor: ");
                    String processor = scanner.nextLine();
                    
                    System.out.print("Graphics Card: ");
                    String graphicsCard = scanner.nextLine();
                    
                    System.out.print("RAM (GB): ");
                    int ramGB = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Storage (GB): ");
                    int storageGB = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Display Size: ");
                    String displaySize = scanner.nextLine();
                    
                    System.out.print("Operating System: ");
                    String operatingSystem = scanner.nextLine();
                    
                    newProduct = new Laptop(productId, productName, productDescription, price, sellingPrice, stock,
                                        processor, graphicsCard, ramGB, storageGB, displaySize, operatingSystem);
                }

                case 2 -> {
                    System.out.println("\n=== Enter Mouse Specific Details ===");
                
                    System.out.print("DPI: ");
                    int dpi = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Is Wireless (true/false): ");
                    boolean isWireless = Boolean.parseBoolean(scanner.nextLine());
                    
                    System.out.print("Number of Buttons: ");
                    int numButtons = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Connectivity (USB/Bluetooth/etc): ");
                    String connectivity = scanner.nextLine();
                    
                    System.out.print("Color: ");
                    String color = scanner.nextLine();
                    
                    newProduct = new Mouse(productId, productName, productDescription, price, sellingPrice, stock,
                                        dpi, isWireless, numButtons, connectivity, color);
                }

                case 3 -> {
                    System.out.println("\n=== Enter Accessory Specific Details ===");
                
                    System.out.print("Compatible With: ");
                    String compatibleWith = scanner.nextLine();
                    
                    System.out.print("Type (Charger/Cable/Case/etc): ");
                    String type = scanner.nextLine();
                    
                    System.out.print("Material: ");
                    String material = scanner.nextLine();
                    
                    System.out.print("Color: ");
                    String color = scanner.nextLine();
                    
                    newProduct = new Accessory(productId, productName, productDescription, price, sellingPrice, stock,
                                            compatibleWith, type, material, color);
                }
            }
            
            if (newProduct != null) {
                products.add(newProduct);
                JsonDataHandler.saveProductsList(products);
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add product. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error adding product: " + e.getMessage());
        } finally {
            MenuUtils.waitEnter(scanner);
        }
    }

    public void updateProduct(List<Product> products) {
        System.out.println("Select product to update.");
        int productChoice = MenuUtils.validateDigit(1, products.size());
        System.out.println(products.size());
        
        if (productChoice < 1 || productChoice > products.size()) {
            System.out.println("Invalid product selection.");
            return;
        }

        // Get the product directly from the list by index (adjust for 0-based indexing)
        int productIndex = productChoice - 1;
        System.out.println(productIndex);
        Product product = products.get(productIndex);
        System.out.println("Current Product Name: " + product.getProdName());
        System.out.println("Current Product Description: " + product.getProdDesc());
        System.out.println("Current Product Price: " + product.getUnitPrice());
        System.out.println("Current Product Selling Price: " + product.getSellingPrice());
        System.out.println("Current Product Stock: " + product.getStock());

        Scanner scanner = MenuUtils.getScanner();
        System.out.print("Enter new Product Name (or press Enter to keep current): ");
        String newProductName = scanner.nextLine();
        if (!newProductName.isEmpty()) product.setProdName(newProductName);

        System.out.print("Enter new Product Description (or press Enter to keep current): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.isEmpty()) product.setProdDesc(newDescription);
        
        System.out.print("Enter new Product Price (or press Enter to keep current): ");
        String priceInput = scanner.nextLine();
        if (!priceInput.isEmpty()) product.setUnitPrice(Double.parseDouble(priceInput));

        System.out.print("Enter new Product Selling Price (or press Enter to keep current): ");
        String sellingPriceInput = scanner.nextLine();
        if (!sellingPriceInput.isEmpty()) product.setSellingPrice(Double.parseDouble(sellingPriceInput));
        
        System.out.print("Enter new Product Stock (or press Enter to keep current): ");
        String stockInput = scanner.nextLine();
        if (!stockInput.isEmpty()) product.setStock(Integer.parseInt(stockInput));
        
        // No need to put back in list, the product reference is already updated
        JsonDataHandler.saveProductsList(products);
        
        System.out.println("Product updated successfully!");
    }

    public void deleteProduct(List<Product> products) {
        System.out.println("Select product to remove.");
        int productIndex = MenuUtils.validateDigit(1, products.size()) - 1;
        
        if (productIndex >= 0 && productIndex < products.size()) {
            products.remove(productIndex);
            JsonDataHandler.saveProductsList(products);
            System.out.println("Product removed successfully!");
        } else {
            System.out.println("Invalid product selection.");
        }
    }




}

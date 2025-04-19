package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

public class UserMenu {
    private Scanner scanner;
    private Map<String, User> customers;
    private Map<String, Admin> admins;
    public static User currentUser = null;
    // Add this as a static instance variable
    private static UserMenu instance;

    // Add a no-arg constructor for the singleton pattern
    public UserMenu() {
        instance = this;
    }

    public UserMenu(Map<String, User> customers, Map<String, Admin> admins) {
        this.scanner = MenuUtils.getScanner();
        this.customers = customers;
        this.admins = admins;
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
                    login(); // The login method now handles directing to appropriate menus
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
            // Create a new customer with a generated ID
            String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Customer customer = new Customer(customerId, customerName, email, password, address, phoneNum);

            // Reload the latest customers data to avoid overwriting
            Map<String, User> latestCustomers = JsonDataHandler.loadCustomers();

            // Add to the latest customers map
            latestCustomers.put(customer.getUserID(), customer);

            // Save to JSON
            JsonDataHandler.saveCustomers(latestCustomers);

            // Update our in-memory map
            customers = latestCustomers;

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

                // Load products immediately after successful login
                System.out.println("Loading product data...");
                Map<String, Product> productMap = JsonDataHandler.loadProducts();
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
            choice = MenuUtils.getMenuChoice(1, 9);

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

                default:
                    System.out.println("Feature not implemented yet.");
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
            System.out.println("No categories available.");
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

            case 4:
                viewAllCategories();
                break;

            case 5:
                System.out.println("Returning to Admin Dashboard...");
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
        // Create categories
        Category laptops = new Category("C01", "Laptops", "Laptops Specs");
        Category mouses = new Category("C02", "Mice", "Mice Specs");
        Category accessories = new Category("C03", "Accessories", "Chargers");

        // Load products from JSON or use sample products if needed
        Map<String, Product> productMap = JsonDataHandler.loadProducts();
        Product[] products;

        if (productMap.isEmpty()) {
            // Use sample products if no products are loaded from JSON
            products = new Product[]{
                    new Laptop("P01", "ROG Gaming Laptop", "High-Performance Laptop", laptops, 7000.0, 10,
                            "Intel i9", "RTX 4080", 32, 1000, "17.3 inch", "Windows 11"),
                    new Mouse("P02", "Logitech G Pro", "Latest Gaming Mouse", mouses, 1000.0, 20,
                            25000, true, 8, "Wireless", "Black"),
                    new Accessory("P03", "Fast Charger", "Super Fast Charging Speed", accessories, 100.0, 40,
                            "All laptops", "Charger", "Plastic", "Black")
            };
        } else {
            // Convert Map to Array
            products = productMap.values().toArray(new Product[0]);
        }

        Productlisting productListing = new Productlisting(products);
        int choice;
        do {
            System.out.println("\nProducts Menu:");
            System.out.println("1. Display All Products");
            System.out.println("2. Search Product by Name");
            System.out.println("3. Search Product by Category");
            System.out.println("4. Sort Products by Price");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");

            choice = MenuUtils.getMenuChoice(1, 5);

            switch (choice) {
                case 1:
                    productListing.displayProducts();
                    break;

                case 2:
                    System.out.print("Enter Product Name to Search: ");
                    String searchName = scanner.nextLine();
                    Product[] foundByName = productListing.searchByName(searchName);
                    for (Product p : foundByName) {
                        System.out.println(p.getDetails());
                    }
                    break;

                case 3:
                    System.out.print("Enter Product Category to Search: ");
                    String searchCategory = scanner.nextLine();
                    Product[] foundByCategory = productListing.searchByCategory(searchCategory);
                    for (Product p : foundByCategory) {
                        System.out.println(p.getDetails());
                    }
                    break;

                case 4:
                    productListing.sortProductsByPrice();
                    break;

                case 5:
                    System.out.println("Returning to main menu...");
                    return;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 5);
    }

    private void manageWishlistMenu() {
        if (!(currentUser instanceof Customer)) {
            System.out.println("Access denied. Customer privileges required.");
            return;
        }

        Customer customer = (Customer) currentUser;
        customer.getWishlist().displayWishlist();
    }

    private void customerMenu() {
        System.out.println("\n=== Customer Menu ===");
        System.out.println("1. View Products");
        System.out.println("2. View Cart");
        System.out.println("3. View Wishlist");
        System.out.println("4. View Order History");
        System.out.println("5. Edit Profile");
        System.out.println("6. Logout");

        int choice = MenuUtils.getMenuChoice(1, 6);

        switch (choice) {
            case 1:
                // Call your existing productMenu method
                productMenu();
                break;
            case 2:
                manageCartMenu();
                break;
            case 3:
                manageWishlistMenu();
                break;
            case 4:
                viewOrderHistory();
                break;
            case 5:
                viewProfile();
                break;
            case 6:
                logout();
                break;
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

        System.out.println("\n=== Manage Cart ===");
        System.out.println("1. Update Item Quantity");
        System.out.println("2. Remove Item");
        System.out.println("3. Clear Cart");
        System.out.println("4. Back");

        int choice = MenuUtils.getMenuChoice(1, 4);

        switch (choice) {
            case 1:
                // Update quantity
                cart.displayCart();
                System.out.print("Enter item index to update: ");
                int updateIndex = MenuUtils.getMenuChoice(1, cart.getItems().size()) - 1;
                System.out.print("Enter new quantity: ");
                int newQuantity = MenuUtils.getMenuChoice(1, 100);
                cart.updateItemQuantity(updateIndex, newQuantity);
                break;

            case 2:
                // Remove item
                cart.displayCart();
                System.out.print("Enter item index to remove: ");
                int removeIndex = MenuUtils.getMenuChoice(1, cart.getItems().size()) - 1;
                cart.removeItem(removeIndex);
                break;

            case 3:
                // Clear cart
                System.out.println("Are you sure you want to clear your cart? (Y/N)");
                if (scanner.nextLine().trim().toUpperCase().equals("Y")) {
                    cart.clearCart();
                }
                break;

            case 4:
                return;
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    // Move the customerHomepage method inside the class
    private void customerHomepage() {
        int choice;
        do {
            System.out.println("\n=== GSports Customer Homepage ===");
            System.out.println("1. Browse Products");
            System.out.println("2. View Wishlist");
            System.out.println("3. View Cart");
            System.out.println("4. Order History");
            System.out.println("5. My Profile");
            System.out.println("6. Logout");
            System.out.println("7. Exit");

            choice = MenuUtils.getMenuChoice(1, 7);

            switch (choice) {
                case 1:
                    productMenu();
                    break;
                case 2:
                    if (currentUser instanceof Customer) {
                        ((Customer) currentUser).getWishlist().displayWishlist();
                    }
                    break;
                case 3:
                    if (currentUser instanceof Customer) {
                        ((Customer) currentUser).getCart().displayCart();

                        // Add option to proceed to checkout or manage cart
                        System.out.println("\n1. Proceed to Checkout");
                        System.out.println("2. Manage Cart");
                        System.out.println("3. Back");

                        int cartChoice = MenuUtils.getMenuChoice(1, 3);

                        switch (cartChoice) {
                            case 1:
                                checkout();
                                break;
                            case 2:
                                manageCartMenu();
                                break;
                            case 3:
                                break;
                        }
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
                    JsonDataHandler.saveCustomers(customers);
                    System.out.println("Thank you for shopping with GSports!");
                    System.out.println("Exiting...");
                    MenuUtils.closeScanner();
                    System.exit(0); // Terminate the program
                    break;
            }
        } while (choice != 6 && choice != 7);
    }

    private void viewProfile() {
        System.out.println("\n=== My Profile ===");
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            System.out.println("User ID: " + customer.getUserID());
            System.out.println("Name: " + customer.getUsername());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Phone: " + customer.getPhoneNum());

            System.out.println("\nWould you like to edit your profile? (Y/N)");
            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("Y")) {
                editProfile(customer);
            }
        }
    }

    private void editProfile(Customer customer) {
        System.out.println("\n=== Edit Profile ===");
        System.out.println("1. Edit Name");
        System.out.println("2. Edit Address");
        System.out.println("3. Edit Phone Number");
        System.out.println("4. Change Password");
        System.out.println("5. Back to Profile");

        int choice = MenuUtils.getMenuChoice(1, 5);

        switch (choice) {
            case 1:
                // Edit name functionality
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                customer.setUsername(newName);
                System.out.println("Name updated successfully!");
                break;
            case 2:
                System.out.print("Enter new address: ");
                String newAddress = scanner.nextLine();
                customer.setAddress(newAddress);
                System.out.println("Address updated successfully!");
                break;
            case 3:
                System.out.print("Enter new phone number: ");
                String newPhone = scanner.nextLine();
                customer.setPhoneNum(newPhone);
                System.out.println("Phone number updated successfully!");
                break;
            case 4:
                // Change password functionality
                System.out.print("Enter current password: ");
                String currentPassword = MenuUtils.maskPassword(scanner);
                if (currentPassword.equals(customer.getPassword())) {
                    System.out.print("Enter new password (minimum 8 characters): ");
                    String newPassword = MenuUtils.maskPassword(scanner);
                    if (newPassword.length() >= 8) {
                        customer.setPassword(newPassword);
                        System.out.println("Password updated successfully!");
                    } else {
                        System.out.println("Password must be at least 8 characters long.");
                    }
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
        List<Order> orders = customer.getOrderHistory();

        if (orders == null || orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }

        System.out.println("Your Order History:");
        System.out.println("------------------");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.println((i + 1) + ". Order ID: " + order.getOrderId() +
                    " | Date: " + order.getFormattedOrderDate() +
                    " | Status: " + order.getStatus() +
                    " | Total: $" + String.format("%.2f", order.getTotalAmount()));
        }

        System.out.println("\nEnter order number to view details (0 to go back): ");
        int choice = MenuUtils.getMenuChoice(0, orders.size());

        if (choice > 0) {
            orders.get(choice - 1).displayOrderDetails();
        }
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

        System.out.println("\n=== Checkout ===");
        System.out.println("Your cart total: $" + String.format("%.2f", cart.getTotalAmount()));

        // Confirm shipping address
        System.out.println("\nCurrent shipping address: " + customer.getAddress());
        System.out.println("Would you like to use this address? (Y/N)");
        String addressChoice = scanner.nextLine().trim().toUpperCase();

        String shippingAddress;
        if (addressChoice.equals("Y")) {
            shippingAddress = customer.getAddress();
        } else {
            System.out.println("Enter new shipping address:");
            shippingAddress = scanner.nextLine().trim();
        }

        // Select payment method
        System.out.println("\nSelect payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. Cash on Delivery");

        int paymentChoice = MenuUtils.getMenuChoice(1, 3);
        String paymentMethod;

        switch (paymentChoice) {
            case 1:
                paymentMethod = "Credit Card";
                break;
            case 2:
                paymentMethod = "PayPal";
                break;
            case 3:
                paymentMethod = "Cash on Delivery";
                break;
            default:
                paymentMethod = "Cash on Delivery";
        }

        // Process payment
        Payment payment = new Payment(
                "ORD-" + System.currentTimeMillis(),
                cart.getTotalAmount(),
                paymentMethod,
                currentUser
        );

        boolean paymentSuccess = payment.processPayment();

        if (paymentSuccess) {
            // Create order
            Order order = new Order(
                    customer.getUserID(),
                    new ArrayList<>(cart.getItems()),
                    cart.getTotalAmount(),
                    shippingAddress,
                    paymentMethod
            );

            // Add order to customer history
            customer.addOrder(order);

            // Update product stock
            Map<String, Product> products = new HashMap<>();
            for (CartItem item : cart.getItems()) {
                Product product = item.getProduct();
                int newStock = product.getStock() - item.getQuantity();
                product.setStock(newStock);
                products.put(product.getProdID(), product);
            }

            // Save updated product data
            JsonDataHandler.saveProducts(products);

            // Clear the cart
            cart.clearCart();

            System.out.println("\nOrder placed successfully!");
        } else {
            System.out.println("\nOrder was not completed due to payment failure.");
        }
    }
}
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
        // Auto-generate product ID by incrementing from the last one
        String productId;
        if (products.isEmpty()) {
            productId = "P001"; // Starting ID if no products exist
        } else {
            // Get the highest existing product ID and increment it
            int lastId = products.values().stream()
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
        
        if (products.containsKey(productId)) {
            System.out.println("Product ID already exists. Please use a different ID.");
            return;
        }
        
        System.out.print("Enter Product Name: ");
        String productName = scanner.nextLine();
        
        System.out.print("Enter Product Description: ");
        String productDescription = scanner.nextLine();
        
        Map<String, Category> categories = JsonDataHandler.loadCategories();
        int index = 1;
        System.out.println("Select Category: ");
        for (Map.Entry<String, Category> entry : categories.entrySet()) {
            System.out.println(index + ". " + entry.getValue().getCategoryName());
            index++;
        }
    
        int categoryChoice = MenuUtils.getMenuChoice(1, categories.size());
        Category category = categories.get((String) categories.keySet().toArray()[categoryChoice - 1]);
        
        if (category == null) {
            System.out.println("Invalid category choice.");
            return;
        }
        System.out.println("Selected category: " + category.getCategoryName());
        
        System.out.print("Enter Product Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Enter Product Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        
        Product newProduct = null;
        
        // Create specific product type based on category
        if (category.getCategoryName().equalsIgnoreCase("Laptops")) {
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
            
            newProduct = new Laptop(productId, productName, productDescription, category, price, stock,
                                  processor, graphicsCard, ramGB, storageGB, displaySize, operatingSystem);
        } 
        else if (category.getCategoryName().equalsIgnoreCase("Mice")) {
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
            
            newProduct = new Mouse(productId, productName, productDescription, category, price, stock,
                                 dpi, isWireless, numButtons, connectivity, color);
        } 
        else if (category.getCategoryName().equalsIgnoreCase("Accessories")) {
            System.out.println("\n=== Enter Accessory Specific Details ===");
            
            System.out.print("Compatible With: ");
            String compatibleWith = scanner.nextLine();
            
            System.out.print("Type (Charger/Cable/Case/etc): ");
            String type = scanner.nextLine();
            
            System.out.print("Material: ");
            String material = scanner.nextLine();
            
            System.out.print("Color: ");
            String color = scanner.nextLine();
            
            newProduct = new Accessory(productId, productName, productDescription, category, price, stock,
                                     compatibleWith, type, material, color);
        }
        
        if (newProduct != null) {
            products.put(productId, newProduct);
            JsonDataHandler.saveProducts(products);
            System.out.println("Product added successfully!");
        } else {
            System.out.println("Failed to create product. Category not supported for specific product types.");
        }
    }

    public void updateProduct(Map<String, Product> products) {
        System.out.println("Select product to update.");
        int productIndex = MenuUtils.getMenuChoice(1, products.size());
        String productId = (String) products.keySet().toArray()[productIndex - 1];

        if (!products.containsKey(productId)) {
            System.out.println("Product ID not found.");
            return;
        }

        // Display current product details
        Product product = products.get(productId);
        System.out.println("Current Product Name: " + product.getProdName());
        System.out.println("Current Product Description: " + product.getProdDesc());
        System.out.println("Current Product Price: " + product.getUnitPrice());
        System.out.println("Current Product Stock: " + product.getStock());
        System.out.println("Current Product Category: " + product.getCategory().getCategoryName());


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
        
        System.out.print("Enter new Product Stock (or press Enter to keep current): ");
        String stockInput = scanner.nextLine();
        if (!stockInput.isEmpty()) product.setStock(Integer.parseInt(stockInput));
        
        products.put(productId, product);
        JsonDataHandler.saveProducts(products);
        
        System.out.println("Product updated successfully!");
    }

    public void deleteProduct(Map<String, Product> products) {
        System.out.println("Select product to remove. ");
        int productIndex = MenuUtils.getMenuChoice(1, products.size());
        String productId = (String) products.keySet().toArray()[productIndex - 1];
        
        if (products.containsKey(productId)) {
            products.remove(productId);
            JsonDataHandler.saveProducts(products);
            System.out.println("Product removed successfully!");
        } else {
            System.out.println("Product ID not found.");
        }
    }

    public void addCategory(Map<String, Category> categories) {
        Scanner scanner = MenuUtils.getScanner();
        System.out.print("Enter Category ID: ");
        String categoryId = scanner.nextLine();
        
        if (categories.containsKey(categoryId)) {
            System.out.println("Category ID already exists. Please use a different ID.");
            return;
        }
        
        System.out.print("Enter Category Name: ");
        String categoryName = scanner.nextLine();
        
        System.out.print("Enter Category Description: ");
        String categoryDescription = scanner.nextLine();
        
        Category newCategory = new Category(categoryId, categoryName, categoryDescription);
        categories.put(categoryId, newCategory);
        JsonDataHandler.saveCategories(categories);
        
        System.out.println("Category added successfully!");
    }

    public void updateCategory(Map<String, Category> categories) {
        System.out.println("Select category to update.");
        int categoryIndex = MenuUtils.getMenuChoice(1, categories.size());
        String categoryId = (String) categories.keySet().toArray()[categoryIndex - 1];

        if (!categories.containsKey(categoryId)) {
            System.out.println("Category ID not found.");
            return;
        }
        Scanner scanner = MenuUtils.getScanner();
        Category category = categories.get(categoryId);
        System.out.println("Current Category Name: " + category.getCategoryName());
        System.out.println("Current Category Description: " + category.getDescription());
        System.out.print("Enter new Category Name (or press Enter to keep current): ");
        String newCategoryName = scanner.nextLine();
        if (!newCategoryName.isEmpty()) category.setCategoryName(newCategoryName);
        System.out.print("Enter new Category Description (or press Enter to keep current): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.isEmpty()) category.setDescription(newDescription);
        categories.put(categoryId, category);
        JsonDataHandler.saveCategories(categories);
        System.out.println("Category updated successfully!");

    }

    public void deleteCategory(Map<String, Category> categories) {
        System.out.println("Select category to remove.");
        int categoryIndex = MenuUtils.getMenuChoice(1, categories.size());
        String categoryId = (String) categories.keySet().toArray()[categoryIndex - 1];
        
        if (categories.containsKey(categoryId)) {
            categories.remove(categoryId);
            JsonDataHandler.saveCategories(categories);
            System.out.println("Category removed successfully!");
        } else {
            System.out.println("Category ID not found.");
        }
    }

}

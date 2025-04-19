package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class JsonDataHandler {
    private static final String CUSTOMER_FILE_PATH = "customers.json";
    private static final ObjectMapper mapper = createObjectMapper();
    
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register the JavaTimeModule to handle Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());
        // Configure to write dates as ISO-8601 strings
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Configure to ignore unknown properties during deserialization
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public static void saveCustomers(Map<String,User> newCustomers) {
        try {
            Map<String, User> existingCustomers = new HashMap<>();
            if (newCustomers != null && !newCustomers.isEmpty()) {
                try {
                    existingCustomers = loadCustomers();
                } catch (Exception e) {
                    System.out.println("Error loading users from customers.json" + e.getMessage());
                }

                existingCustomers.putAll(newCustomers);

                if (!existingCustomers.isEmpty()) {
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.writeValue(new File(CUSTOMER_FILE_PATH), existingCustomers);
                    System.out.println("Customer data is saved to " + CUSTOMER_FILE_PATH);
                } else {
                    System.out.println("Customer data is empty");
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving customer data: " + e.getMessage());
        }
    }

    // Update the loadCustomers method to use the configured mapper
    public static Map<String, User> loadCustomers() {
        try {
            File file = new File(CUSTOMER_FILE_PATH);
            if (file.exists()) {
                ObjectMapper readerMapper = mapper.copy();
                
                return readerMapper.readValue(file,
                        readerMapper.getTypeFactory().constructMapType(HashMap.class, String.class, User.class));
            } else {
                System.out.println("No existing customer data found. Starting with an empty dataset.");
                return new HashMap<>();
            }
        } catch (IOException e) {
            System.out.println("Error loading customer data: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveAdmins(Map<String, Admin> admins) {
        try {
            // Use the configured mapper instead of creating a new one
            ObjectMapper adminMapper = mapper.copy();
            File file = new File("admins.json");
            adminMapper.enable(SerializationFeature.INDENT_OUTPUT);
            adminMapper.writeValue(file, admins);
        } catch (IOException e) {
            System.out.println("Error saving admin data: " + e.getMessage());
        }
    }

    public static Map<String, Admin> loadAdmins() {
        Map<String, Admin> admins = new HashMap<>();
        try {
            File file = new File("admins.json");
            if (file.exists()) {
                ObjectMapper readerMapper = mapper.copy();
                admins = readerMapper.readValue(file, 
                        readerMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Admin.class));
            } else {
                Admin defaultAdmin = new Admin("A001", "admin", "admin@gmail.com", "admin123", "superadmin");
                admins.put(defaultAdmin.getUserID(), defaultAdmin);
                saveAdmins(admins);
            }
        } catch (IOException e) {
            System.out.println("Error loading admin data: " + e.getMessage());
        }
        return admins;
    }

    public static void saveProducts(Map<String, Product> products) {
        try {
            // Use the configured mapper instead of creating a new one
            ObjectMapper productMapper = mapper.copy();
            File file = new File("products.json");
            productMapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            // Make sure we're using the type info from the Product class annotations
            // No need to disable default typing as it's not enabled by default
            
            productMapper.writeValue(file, products);
            System.out.println("Product data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving product data: " + e.getMessage());
            e.printStackTrace(); // Add this to see the full stack trace
        }
    }

    public static Map<String, Product> loadProducts() {
        Map<String, Product> products = new HashMap<>();
        try {
            File file = new File("products.json");
            if (file.exists()) {
                try {
                    // Use the configured mapper instead of creating a new one
                    ObjectMapper readerMapper = mapper.copy();
                    
                    // Configure the mapper to be more lenient
                    readerMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    
                    // Import these at the top of the file
                    // import com.fasterxml.jackson.databind.JsonNode;
                    // import com.fasterxml.jackson.databind.node.ObjectNode;
                    // import java.util.Iterator;
                    
                    // Read the JSON as a generic tree first
                    com.fasterxml.jackson.databind.JsonNode rootNode = readerMapper.readTree(file);
                    
                    // Process each product entry
                    Iterator<Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> fields = rootNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> entry = fields.next();
                        String productId = entry.getKey();
                        com.fasterxml.jackson.databind.JsonNode productNode = entry.getValue();
                        
                        // Determine product type based on fields present
                        String productType = determineProductType(productNode);
                        
                        // Create a wrapper object with the type as key
                        com.fasterxml.jackson.databind.node.ObjectNode wrapperNode = readerMapper.createObjectNode();
                        wrapperNode.set(productType, productNode);
                        
                        // Deserialize with the wrapper
                        Product product = readerMapper.treeToValue(wrapperNode, Product.class);
                        products.put(productId, product);
                    }
                    
                    // If we get here, the file was read successfully
                    System.out.println("Successfully loaded " + products.size() + " products.");
                    
                } catch (Exception e) {
                    System.out.println("Error parsing existing product data: " + e.getMessage());
                    System.out.println("The file format appears to be incompatible. Creating new product data...");
                    // Delete the corrupted file to start fresh
                    file.delete();
                }
            }
            
            // If products is still empty (file didn't exist or couldn't be parsed)
            if (products.isEmpty()) {
                // Create default products with categories
                createDefaultProducts(products);
                // Save the newly created products
                saveProducts(products);
                System.out.println("Created default products with proper type information.");
            }
            return products;
        } catch (Exception e) {
            System.out.println("Error loading product data: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Helper method to determine product type based on fields present
    private static String determineProductType(com.fasterxml.jackson.databind.JsonNode node) {
        if (node.has("processor") && node.has("graphicsCard") && node.has("ramGB")) {
            return "laptop";
        } else if (node.has("dpi") && node.has("numButtons")) {
            return "mouse";
        } else if (node.has("compatibleWith") && node.has("type") && node.has("material")) {
            return "accessory";
        }
        // Default to accessory if can't determine
        return "accessory";
    }

    // Helper method to create default products
    private static void createDefaultProducts(Map<String, Product> products) {
        // Create default categories
        Category laptopCategory = new Category("C01", "Laptops", "Laptops Specs");
        Category mouseCategory = new Category("C02", "Mice", "Mice Specs");
        Category accessoryCategory = new Category("C03", "Accessories", "Chargers");
        
        // Save categories
        Map<String, Category> categories = new HashMap<>();
        categories.put(laptopCategory.getCategoryID(), laptopCategory);
        categories.put(mouseCategory.getCategoryID(), mouseCategory);
        categories.put(accessoryCategory.getCategoryID(), accessoryCategory);
        saveCategories(categories);
        
        // Create default products
        Laptop defaultLaptop = new Laptop("P001", "Default Laptop", "High-end laptop", 
                                        laptopCategory, 1500.00, 20, "Intel i7", 
                                        "NVIDIA RTX 3060", 16, 512, "15.6 inch", "Windows 11");
        
        Mouse defaultMouse = new Mouse("P002", "Default Mouse", "Gaming mouse", 
                                     mouseCategory, 99.99, 30, 16000, true, 
                                     8, "Bluetooth", "Black");
        
        Accessory defaultAccessory = new Accessory("P003", "Default Charger", "Fast charger", 
                                                accessoryCategory, 49.99, 50, "All laptops", 
                                                "Charger", "Plastic", "White");
        
        products.put(defaultLaptop.getProdID(), defaultLaptop);
        products.put(defaultMouse.getProdID(), defaultMouse);
        products.put(defaultAccessory.getProdID(), defaultAccessory);
        
        saveProducts(products);
        System.out.println("Created default products with proper type information.");
    }

    // load categories
    public static Map<String, Category> loadCategories() {
        Map<String, Category> categories = new HashMap<>();
        try {
            File file = new File("categories.json");
            if (file.exists()) {
                ObjectMapper readerMapper = mapper.copy();
                categories = readerMapper.readValue(file,
                        readerMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Category.class));
            } else {
                Category[] defaultCategories = {
                        new Category("C01", "Laptops", "Laptops Specs"),
                        new Category("C02", "Mouses", "Mouses Specs"),
                        new Category("C03", "Accessories", "Chargers")
                };
                for (Category category: defaultCategories) {
                    categories.put(category.getCategoryID(), category);
                }
                saveCategories(categories);
                System.out.println("No existing category data found. Starting with an empty dataset.");
            }
            return categories;
        } catch (IOException e) {
            System.out.println("Error loading category data: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveCategories(Map<String, Category> categories) {
        try {
            // Use the configured mapper instead of creating a new one
            ObjectMapper categoryMapper = mapper.copy();
            File file = new File("categories.json");
            categoryMapper.enable(SerializationFeature.INDENT_OUTPUT);
            categoryMapper.writeValue(file, categories);
        } catch (IOException e) {
            System.out.println("Error saving category data: " + e.getMessage());
        }
    }
}

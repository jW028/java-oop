package com.gsports.java.oop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Locale.Category;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.annotation.JsonInclude;


public class JsonDataHandler {
    private static final String CUSTOMER_FILE_PATH = "customers.json";
    private static final String ADMIN_FILE_PATH = "admins.json";
    private static final String PRODUCT_FILE_PATH = "products.json";
    private static final String ORDER_FILE_PATH = "orders.json";
    private static final String PAYMENTS_FILE_PATH = "payments.json";
    private static final ObjectMapper mapper = createObjectMapper();
    
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register the JavaTimeModule to handle Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());
        // Configure to write dates as ISO-8601 strings
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Configure to ignore unknown properties during deserialization
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    public static void saveCustomers(Map<String,User> customers) {
        try {
            if (customers != null && !customers.isEmpty()) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(new File(CUSTOMER_FILE_PATH), customers);
                }
                else {
                   System.out.println("Customer data is empty");
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
            File file = new File(ADMIN_FILE_PATH);
            adminMapper.enable(SerializationFeature.INDENT_OUTPUT);
            adminMapper.writeValue(file, admins);
        } catch (IOException e) {
            System.out.println("Error saving admin data: " + e.getMessage());
        }
    }

    public static Map<String, Admin> loadAdmins() {
        Map<String, Admin> admins = new HashMap<>();
        try {
            File file = new File(ADMIN_FILE_PATH);
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
            File file = new File(PRODUCT_FILE_PATH);
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
            File file = new File(PRODUCT_FILE_PATH);
            if (file.exists()) {
                try {
                    // Use the configured mapper instead of creating a new one
                    ObjectMapper readerMapper = mapper.copy();
                    
                    // Configure the mapper to be more lenient
                    readerMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    
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
                // loadProducts again to ensure they are loaded correctly
                products = loadProducts();
                System.out.println("Created default products with proper type information.");
            }
            return products;
        } catch (Exception e) {
            System.out.println("Error loading product data: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Helper method to create default products
    private static void createDefaultProducts(Map<String, Product> products) {
        
        Laptop defaultLaptop = new Laptop("P001", "Asus ROG Strix", "High-performance gaming laptop", 
                                    6299.99, 7199.00, 20, "Intel i9", 
                                    "NVIDIA RTX 3060", 16, 512, "15.6 inch", "Windows 11");
    
        Mouse defaultMouse = new Mouse("P002", "Logitech G Pro", "Logitech gaming mouse", 
                                    79.99, 99.99, 30, 16000, true, 
                                    8, "Bluetooth", "Black");
        
        Accessory defaultAccessory = new Accessory("P003", "Fast Charger", "20W fast charger", 
                                                39.99, 49.99, 50, "All laptops", 
                                                "Charger", "Plastic", "White");
        
        products.put(defaultLaptop.getProdID(), defaultLaptop);
        products.put(defaultMouse.getProdID(), defaultMouse);
        products.put(defaultAccessory.getProdID(), defaultAccessory);
        
        saveProducts(products);
        System.out.println("Created default products with proper type information.");
    }

    private static String determineProductType(com.fasterxml.jackson.databind.JsonNode node) {
        if (node.has("processor") && node.has("graphicsCard") && node.has("ramGB")) {
            return "Laptop";
        } else if (node.has("dpi") && node.has("numButtons")) {
            return "Mouse";
        } else if (node.has("compatibleWith") && node.has("type") && node.has("material")) {
            return "Accessory";
        }
        // Default to accessory if can't determine
        return "Accessory";
    }


    // Customer adapters
    public static List<User> getCustomersList() {
        return new ArrayList<>(loadCustomers().values());
    }

    public static void saveCustomersList(List<User> customersList) {
        Map<String, User> customersMap = new HashMap<>();
        for (User user : customersList) {
            customersMap.put(user.getUserID(), user);
        }
        saveCustomers(customersMap);
    }

    // Admin adapters
    public static List<Admin> getAdminsList() {
        return new ArrayList<>(loadAdmins().values());
    }

    public static void saveAdminsList(List<Admin> adminsList) {
        Map<String, Admin> adminsMap = new HashMap<>();
        for (Admin admin : adminsList) {
            adminsMap.put(admin.getUserID(), admin);
        }
        saveAdmins(adminsMap);
    }

    // Product adapters
    public static List<Product> getProductsList() {
        return new ArrayList<>(loadProducts().values());
    }

    public static void saveProductsList(List<Product> productsList) {
        Map<String, Product> productsMap = new HashMap<>();
        for (Product product : productsList) {
            productsMap.put(product.getProdID(), product);
        }
        saveProducts(productsMap);
    }

    public static void saveOrders(Map<String, Order> orders) {
        try {
            ObjectMapper orderMapper = mapper.copy();
            File file = new File(ORDER_FILE_PATH);
            orderMapper.enable(SerializationFeature.INDENT_OUTPUT);
            orderMapper.writeValue(file, orders);
        } catch (IOException e) {
            System.out.println("Error saving order data: " + e.getMessage());
        }
    }

    public static Map<String, Order> loadOrders() {
        Map<String, Order> orders = new HashMap<>();
        try {
            File file = new File(ORDER_FILE_PATH);
            if (file.exists()) {
                ObjectMapper readerMapper = mapper.copy();
                orders = readerMapper.readValue(file, 
                        readerMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Order.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading order data: " + e.getMessage());
        }
        return orders;
    }

    public static void saveOrdersList(List<Order> ordersList) {
        Map<String, Order> ordersMap = new HashMap<>();
        for (Order order : ordersList) {
            ordersMap.put(order.getOrderId(), order);
        }
        saveOrders(ordersMap);
    }

    public static List<Order> getOrdersList() {
        return new ArrayList<>(loadOrders().values());
    }

    public static List<Order> getOrderHistory(String customerId) {
        List<Order> orderHistory = getOrdersList();

        return orderHistory.stream()
                .filter(order -> (order.getCustomer().getUserID()).equals(customerId))
                .collect(Collectors.toList());
    }

    public static void savePayments(Map<String, Payment> payments) {
        try {
            ObjectMapper paymentMapper = mapper.copy();
            File file = new File(PAYMENTS_FILE_PATH);
            paymentMapper.enable(SerializationFeature.INDENT_OUTPUT);
            paymentMapper.writeValue(file, payments);
        } catch (IOException e) {
            System.out.println("Error saving payment data: " + e.getMessage());
        }
    }

    public static Map<String, Payment> loadPayments() {
        Map<String, Payment> payments = new HashMap<>();
        try {
            File file = new File(PAYMENTS_FILE_PATH);
            if (file.exists()) {
                ObjectMapper readerMapper = mapper.copy();
                payments = readerMapper.readValue(file, 
                        readerMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Payment.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading payment data: " + e.getMessage());
        }
        return payments;
    }

    public static void savePaymentsList(List<Payment> paymentsList) {
        Map<String, Payment> paymentsMap = new HashMap<>();
        for (Payment payment : paymentsList) {
            paymentsMap.put(payment.getPaymentId(), payment);
        }
        savePayments(paymentsMap);
    }

    public static List<Payment> getPaymentsList() {
        return new ArrayList<>(loadPayments().values());
    }
}

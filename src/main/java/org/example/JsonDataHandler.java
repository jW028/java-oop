package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonDataHandler {
    private static final String CUSTOMER_FILE_PATH = "customers.json";
    private static final ObjectMapper mapper = new ObjectMapper();

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
            ObjectMapper mapper = new ObjectMapper();
            File file = new File ("admins.json");
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(file, admins);
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

    public static Map<String, Product> loadProducts() {
        Map<String, Product> products = new HashMap<>();
        try {
            File file = new File("products.json");
            if (file.exists()) {
                ObjectMapper readerMapper = mapper.copy();
                products = readerMapper.readValue(file,
                        readerMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Product.class));
            } else {
                Product defaultProduct = new Product("P001", "Laptop", "High-end laptop", new Category("C01", "Laptops", "Laptops Specs"), 1500.00, 20);
                products.put(defaultProduct.getProdID(), defaultProduct);
                saveProducts(products);
                System.out.println("No existing product data found. Starting with an empty dataset.");
            }
            return products;
        } catch (IOException e) {
            System.out.println("Error loading product data: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveProducts(Map<String, Product> products) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("products.json");
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(file, products);
        } catch (IOException e) {
            System.out.println("Error saving product data: " + e.getMessage());
        }
    }
}

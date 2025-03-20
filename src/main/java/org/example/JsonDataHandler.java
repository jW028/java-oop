package org.example;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
}

package org.example;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GeminiService {
    private static GeminiService instance;
    private Client client;
    private final int maxRetries = 3;
    private final String model = "gemini-2.0-flash-001";

    
    private GeminiService() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            
            if (input == null) {
                System.err.println("Unable to find config.properties");
                return;
            }
            
            props.load(input);
            String apiKey = props.getProperty("gemini.api.key");
            
            this.client = Client.builder()
                    .apiKey(apiKey)
                    .build();
                    
        } catch (IOException ex) {
            System.err.println("Error loading properties file: " + ex.getMessage());
        }
    }

    public static synchronized GeminiService getInstance() {
        if (instance == null) {
            instance = new GeminiService();
        }
        return instance;
    }

    public String askAboutProduct (String question, Product product) {
        String prompt = "You are a retail assistant for GSports Retail System.\n" +
                        "Please provide helpful information about this product:\n\n" +
                        "Product ID: " + product.getProdID() + "\n" +
                        "Name: " + product.getProdName() + "\n" +
                        "Description: " + product.getProdDesc() + "\n" +
                        "Category: " + product.getCategory().getCategoryName() + "\n" +
                        "Price: $" + product.getUnitPrice() + "\n" +
                        "Stock: " + product.getStock() + "\n\n" +
                        "Customer question: " + question + "\n\n" +
                        "Please provide a helpful, accurate and concise answer.";

        return generateResponse(prompt);
    }

    public String getProductRecommendations(Map<String, Product> products, String userPreference) {
        StringBuilder productInfo = new StringBuilder();
        int count = 0;
        
        // Include up to 5 products to avoid token limits
        for (Product product : products.values()) {
            if (count++ >= 5) break;
            
            productInfo.append("Product ID: ").append(product.getProdID())
                    .append(", Name: ").append(product.getProdName())
                    .append(", Category: ").append(product.getCategory().getCategoryName())
                    .append(", Price: $").append(product.getUnitPrice())
                    .append("\n");
        }

        String prompt = "You are a retail assistant for GSports Retail System.\n" +
                "Based on these products:\n\n" + productInfo + 
                "\nAnd the customer's preference: \"" + userPreference + "\"\n\n" +
                "Recommend the best product and explain why in 2-3 sentences.";

        return generateResponse(prompt);
    }

    public String compareProducts(Product product1, Product product2) {
        String prompt = "Compare these two products and highlight the key differences:\n\n" +
                "Product 1:\n" +
                "Name: " + product1.getProdName() + "\n" +
                "Description: " + product1.getProdDesc() + "\n" +
                "Price: $" + product1.getUnitPrice() + "\n\n" +
                "Product 2:\n" +
                "Name: " + product2.getProdName() + "\n" +
                "Description: " + product2.getProdDesc() + "\n" +
                "Price: $" + product2.getUnitPrice() + "\n\n" +
                "Please provide a balanced comparison in bullet points.";

        return generateResponse(prompt);
    }

    private String generateResponse(String prompt) {
        long retryDelayMs = 1000;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    System.out.println("Retry attempt " + attempt + ", waiting " + retryDelayMs + "ms...");
                    Thread.sleep(retryDelayMs);
                    retryDelayMs *= 2; // Exponential backoff
                }

                GenerateContentResponse response = client.models.generateContent(model, prompt, null);
                return response.text();
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("429 Too Many Requests")) {
                    if (attempt == maxRetries) {
                        return "Sorry, the AI service is currently busy. Please try again later.";
                    }
                } else {
                    e.printStackTrace();
                    return "Sorry, I encountered an error: " + e.getMessage();
                }
            }
        }
        return "Failed to generate a response after multiple attempts.";
    }
}

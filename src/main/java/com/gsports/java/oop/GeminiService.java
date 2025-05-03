package com.gsports.java.oop;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiService {
    private ProductListing productListing;
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

    public String processQuery(String query) {
        String prompt = createPromptWithContext(query);
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
                return formatResponse(response.text(), 80);
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
    
    private String createPromptWithContext(String userQuery) {
        StringBuilder contextPrompt = new StringBuilder();
    
        // Instructions
        contextPrompt.append("Your name is GBot and you are an AI assistant for a tech retail shop that sells laptops, mice and accessories. ")
                .append("Answer the customer's question based on the available products and information below. ")
                .append("Keep your answers friendly, concise, and helpful. ")
                .append("If you don't know something specific about our products, be honest but helpful. ")
                .append("Only mention products that are actually in our inventory.\n\n");
    
        // Product listings
        contextPrompt.append("AVAILABLE PRODUCTS:\n");
        List<Product> products = productListing.getProducts();
        
        if (products != null && !products.isEmpty()) {
            // First gather products by type
            List<Laptop> laptops = new ArrayList<>();
            List<Mouse> mice = new ArrayList<>();
            List<Accessory> accessories = new ArrayList<>();
            
            for (Product p : products) {
                if (p instanceof Laptop) laptops.add((Laptop)p);
                else if (p instanceof Mouse) mice.add((Mouse)p);
                else if (p instanceof Accessory) accessories.add((Accessory)p);
            }
            
            // Display laptops
            if (!laptops.isEmpty()) {
                contextPrompt.append("Laptops:\n");
                for (Laptop l : laptops) {
                    try {
                        contextPrompt.append(String.format(
                            "- %s (ID: %s): %s processor, %dGB RAM, %dGB storage, %s graphics, RM %.2f, stock: %d%n",
                            l.getProdName() != null ? l.getProdName() : "N/A",
                            l.getProdID() != null ? l.getProdID() : "N/A",
                            l.getProcessor() != null ? l.getProcessor() : "N/A",
                            l.getRamGB(),
                            l.getStorageGB(),
                            l.getGraphicsCard() != null ? l.getGraphicsCard() : "N/A",
                            l.getUnitPrice(),
                            l.getStock()
                        ));
                    } catch (Exception e) {
                        System.err.println("Error formatting laptop: " + e.getMessage());
                        // Add simple fallback format
                        contextPrompt.append("- ").append(l.getProdName()).append(" (ID: ").append(l.getProdID()).append(")\n");
                    }
                }
            }
            
            // Display mice
            if (!mice.isEmpty()) {
                contextPrompt.append("\nMice:\n");
                for (Mouse m : mice) {
                    try {
                        contextPrompt.append(String.format(
                            "- %s (ID: %s): %d DPI, %s, RM %.2f, stock: %d%n",
                            m.getProdName() != null ? m.getProdName() : "N/A",
                            m.getProdID() != null ? m.getProdID() : "N/A",
                            m.getDpi(),
                            m.isWireless() ? "Wireless" : "Wired",
                            m.getUnitPrice(),
                            m.getStock()
                        ));
                    } catch (Exception e) {
                        System.err.println("Error formatting mouse: " + e.getMessage());
                        contextPrompt.append("- ").append(m.getProdName()).append(" (ID: ").append(m.getProdID()).append(")\n");
                    }
                }
            }
            
            // Display accessories
            if (!accessories.isEmpty()) {
                contextPrompt.append("\nAccessories:\n");
                for (Accessory a : accessories) {
                    try {
                        contextPrompt.append(String.format(
                            "- %s (ID: %s): Type: %s, RM %.2f, stock: %d%n",
                            a.getProdName() != null ? a.getProdName() : "N/A",
                            a.getProdID() != null ? a.getProdID() : "N/A",
                            a.getType() != null ? a.getType() : "N/A",
                            a.getUnitPrice(),
                            a.getStock()
                        ));
                    } catch (Exception e) {
                        System.err.println("Error formatting accessory: " + e.getMessage());
                        contextPrompt.append("- ").append(a.getProdName()).append(" (ID: ").append(a.getProdID()).append(")\n");
                    }
                }
            }
        } else {
            contextPrompt.append("Currently no products in inventory.\n");
        }
    
        // Shop policies
        contextPrompt.append("\nSHOP POLICIES:\n")
                .append("- Warranty: 1-year standard warranty on laptops\n")
                .append("- Payment: Credit cards, e-wallet\n\n")
                .append("CUSTOMER QUESTION: ").append(userQuery).append("\n\n")
                .append("YOUR RESPONSE (friendly, helpful, based only on the information provided):");
    
        return contextPrompt.toString();
    }

    
    private String formatResponse(String text, int lineWidth) {
        // Handle null or empty text
        if (text == null || text.isEmpty()) {
            return "[No response]";
        }
        
        StringBuilder result = new StringBuilder();
        
        // Split text by existing line breaks first
        String[] paragraphs = text.split("\n");
        
        for (int p = 0; p < paragraphs.length; p++) {
            String paragraph = paragraphs[p];
            
            // Skip empty paragraphs but preserve line breaks
            if (paragraph.trim().isEmpty()) {
                result.append("\n");
                continue;
            }
            
            // Create a line wrapper
            int currentLinePosition = 0;
            StringBuilder currentLine = new StringBuilder();
            
            // Split paragraph into words
            String[] words = paragraph.split("\\s+");
            
            for (String word : words) {
                // If adding this word would exceed the line width
                if (currentLinePosition + word.length() > lineWidth && currentLinePosition > 0) {
                    // Add current line to result and start a new one
                    result.append(currentLine.toString()).append("\n");
                    currentLine = new StringBuilder();
                    currentLinePosition = 0;
                }
                
                // Add word to current line
                if (currentLinePosition > 0) {
                    currentLine.append(" ");
                    currentLinePosition++;
                }
                currentLine.append(word);
                currentLinePosition += word.length();
            }
            
            // Add the last line of the paragraph
            if (currentLine.length() > 0) {
                result.append(currentLine.toString());
            }
            
            // Add paragraph break if this isn't the last paragraph
            if (p < paragraphs.length - 1) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
    }


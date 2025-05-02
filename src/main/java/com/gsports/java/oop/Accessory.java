package com.gsports.java.oop;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Accessory")
public class Accessory extends Product{
    private String compatibleWith;
    private String type; // Charger, Cable, Case, etc.
    private String material;
    private String color;

    public Accessory() {
        super();
    }

    public Accessory(String prodID, String prodName, String prodDesc, double unitPrice, 
                    double sellingPrice, int stock, String compatibleWith, String type,
                    String material, String color) {
        super(prodID, prodName, prodDesc, unitPrice, sellingPrice, stock);
        this.compatibleWith = compatibleWith;
        this.type = type;
        this.material = material;
        this.color = color;
    }

    // Getters and Setters
    public String getCompatibleWith() {
        return compatibleWith;
    }
    
    public void setCompatibleWith(String compatibleWith) {
        this.compatibleWith = compatibleWith;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getDetails() {
        // Call the parent class's getDetails method for common product details
        String parentDetails = super.getDetails();

        // Add Accessory-specific details
        String accessoryDetails = String.format("""
                            │ Compatible With: %-30s │
                            │ Type           : %-30s │
                            │ Material       : %-30s │
                            │ Color          : %-30s │""",
            this.compatibleWith,
            this.type,
            this.material,
            this.color
        );

        // Combine parent details with accessory-specific details
        return parentDetails + "\n" + accessoryDetails;
    }

    @Override
    public String getProductType() {
        return "Accessory";
    }
}

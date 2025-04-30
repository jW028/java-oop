package org.example;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("accessory")
public class Accessory extends Product {
    private String compatibleWith;
    private String type; // Charger, Cable, Case, etc.
    private String material;
    private String color;
    
    public Accessory() {
        super();
    }
    
    public Accessory(String prodID, String prodName, String prodDesc, Category category, 
                    double unitPrice, int stock, String compatibleWith, String type,
                    String material, String color) {
        super(prodID, prodName, prodDesc, category, unitPrice, stock);
        this.compatibleWith = compatibleWith;
        this.type = type;
        this.material = material;
        this.color = color;
    }
    
    @Override
    public String getSpecificDetails() {
        return String.format(
                "\n╔═════════════════════════════════════════╗\n" +
                        "║         ACCESSORY SPECIFICATIONS        ║\n" +
                        "╠══════════════════╦══════════════════════╣\n" +
                        "║ %-16s ║ %-2s\n" +
                        "║ %-16s ║ %-2s\n" +
                        "║ %-16s ║ %-2s\n" +
                        "║ %-16s ║ %-2s\n" +
                        "╚══════════════════╩══════════════════════╝",
                "Compatible With:", compatibleWith,
                "Type:", type,
                "Material:", material,
                "Color:", color
        );
    }
    
    @Override
    public String getDetails() {
        return super.getDetails() + "\n\n" + getSpecificDetails();
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
    public String getProductType() {
        return "accessory";
    }
}
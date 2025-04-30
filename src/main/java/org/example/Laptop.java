package org.example;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("laptop")
public class Laptop extends Product {
    private String processor;
    private String graphicsCard;
    private int ramGB;
    private int storageGB;
    private String displaySize;
    private String operatingSystem;
    
    public Laptop() {
        super();
    }
    
    public Laptop(String prodID, String prodName, String prodDesc, Category category, 
                 double unitPrice, int stock, String processor, String graphicsCard, 
                 int ramGB, int storageGB, String displaySize, String operatingSystem) {
        super(prodID, prodName, prodDesc, category, unitPrice, stock);
        this.processor = processor;
        this.graphicsCard = graphicsCard;
        this.ramGB = ramGB;
        this.storageGB = storageGB;
        this.displaySize = displaySize;
        this.operatingSystem = operatingSystem;
    }
    
    @Override
    public String getSpecificDetails() {
        return String.format(
                "\n╔═════════════════════════════════════════╗\n" +
                        "║          LAPTOP SPECIFICATIONS          ║\n" +
                        "╠══════════════════╦══════════════════════╣\n" +
                        "║ %-16s ║ %-2s \n" +
                        "║ %-16s ║ %-2s \n" +
                        "║ %-16s ║ %-2dGB \n" +
                        "║ %-16s ║ %-2dGB \n" +
                        "║ %-16s ║ %-2s \n" +
                        "║ %-16s ║ %-2s \n" +
                        "╚══════════════════╩══════════════════════╝",
                "Processor:", processor,
                "Graphics:", graphicsCard,
                "RAM:", ramGB,
                "Storage:", storageGB,
                "Display:", displaySize,
                "OS:", operatingSystem
        );
    }
    
    @Override
    public String getDetails() {
        return super.getDetails() + "\n\n" + getSpecificDetails();
    }
    
    // Getters and Setters
    public String getProcessor() {
        return processor;
    }
    
    public void setProcessor(String processor) {
        this.processor = processor;
    }
    
    public String getGraphicsCard() {
        return graphicsCard;
    }
    
    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard;
    }
    
    public int getRamGB() {
        return ramGB;
    }
    
    public void setRamGB(int ramGB) {
        this.ramGB = ramGB;
    }
    
    public int getStorageGB() {
        return storageGB;
    }
    
    public void setStorageGB(int storageGB) {
        this.storageGB = storageGB;
    }
    
    public String getDisplaySize() {
        return displaySize;
    }
    
    public void setDisplaySize(String displaySize) {
        this.displaySize = displaySize;
    }
    
    public String getOperatingSystem() {
        return operatingSystem;
    }
    
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
    
    @Override
    public String getProductType() {
        return "laptop";
    }
}
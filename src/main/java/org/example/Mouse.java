package org.example;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("mouse")
public class Mouse extends Product {
    private int dpi;
    private boolean isWireless;
    private int numButtons;
    private String connectivity; // USB, Bluetooth, etc.
    private String color;
    
    public Mouse() {
        super();
    }
    
    public Mouse(String prodID, String prodName, String prodDesc, Category category, 
                double unitPrice, int stock, int dpi, boolean isWireless, 
                int numButtons, String connectivity, String color) {
        super(prodID, prodName, prodDesc, category, unitPrice, stock);
        this.dpi = dpi;
        this.isWireless = isWireless;
        this.numButtons = numButtons;
        this.connectivity = connectivity;
        this.color = color;
    }
    
    @Override
    public String getSpecificDetails() {
        return "DPI: " + dpi + 
               "\nWireless: " + (isWireless ? "Yes" : "No") + 
               "\nButtons: " + numButtons +
               "\nConnectivity: " + connectivity +
               "\nColor: " + color;
    }
    
    @Override
    public String getDetails() {
        return super.getDetails() + "\n\n" + getSpecificDetails();
    }
    
    @Override
    public String getProductType() {
        return "mouse";
    }
    
    // Getters and Setters
    public int getDpi() {
        return dpi;
    }
    
    public void setDpi(int dpi) {
        this.dpi = dpi;
    }
    
    public boolean isWireless() {
        return isWireless;
    }
    
    public void setWireless(boolean wireless) {
        isWireless = wireless;
    }
    
    public int getNumButtons() {
        return numButtons;
    }
    
    public void setNumButtons(int numButtons) {
        this.numButtons = numButtons;
    }
    
    public String getConnectivity() {
        return connectivity;
    }
    
    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
}
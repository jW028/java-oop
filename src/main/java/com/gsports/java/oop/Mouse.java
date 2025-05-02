package com.gsports.java.oop;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Mouse")

public class Mouse extends Product{
    private int dpi;
    private boolean isWireless;
    private int numButtons;
    private String connectivity; // USB, Bluetooth, etc.
    private String color;

    public Mouse() {
        super();
    }

    public Mouse(String prodID, String prodName, String prodDesc, double unitPrice,
                 double sellingPrice, int stock, int dpi, boolean isWireless, 
                int numButtons, String connectivity, String color) {
        super(prodID, prodName, prodDesc, unitPrice, sellingPrice, stock);
        this.dpi = dpi;
        this.isWireless = isWireless;
        this.numButtons = numButtons;
        this.connectivity = connectivity;
        this.color = color;
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

    @Override
    public String getDetails() {
        // Call the parent class's getDetails method for common product details
        String parentDetails = super.getDetails();

        // Add Mouse-specific details
        String mouseDetails = String.format("""
                                │ DPI            : %-30d │
                                │ Wireless       : %-30s │
                                │ Num of Buttons : %-30d │
                                │ Connectivity   : %-30s │
                                │ Color          : %-30s │""",
            this.dpi,
            (this.isWireless ? "Yes" : "No"),
            this.numButtons,
            this.connectivity,
            this.color
        );

        // Combine parent details with mouse-specific details
        return parentDetails + "\n" + mouseDetails;
    }

    @Override
    public String getProductType() {
        return "Mouse";
    }
}

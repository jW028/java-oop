package com.gsports.java.oop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,  // Changed from PROPERTY to WRAPPER_OBJECT
    property = "productType"
)

@JsonSubTypes({
    @JsonSubTypes.Type(value = Laptop.class, name = "Laptop"),
    @JsonSubTypes.Type(value = Mouse.class, name = "Mouse"),
    @JsonSubTypes.Type(value = Accessory.class, name = "Accessory")
})

/**
 *
 * @author jw
 */
public abstract class Product {
    protected String prodID;
    protected String prodName;
    protected String prodDesc;
    protected double unitPrice;
    protected double sellingPrice;
    protected int stock;

    
    public Product() {
    }
    
    public Product(String proID, String prodName, String productDesc, double unitPrice, double sellingPrice, int stock) {
        this.prodID = proID;
        this.prodName = prodName;
        this.prodDesc = productDesc;
        this.unitPrice = unitPrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    // Getters and Setters
    public String getProdID() {
        return prodID;
    }

    public String getProdName() {
        return prodName;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            this.stock = 0;
            System.out.println("Warning: " + this.prodName + " is now out of stock!");
        } else {
            this.stock = stock;
        }
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public static class Utils {
        public static String wrapText(String text, int maxLineLength) {
            StringBuilder wrappedText = new StringBuilder();
            int length = text.length();
            for (int i = 0; i < length; i += maxLineLength) {
                int end = Math.min(i + maxLineLength, length);
                wrappedText.append(text, i, end).append("\n");
            }
            return wrappedText.toString().trim();
        }
    }

    @JsonIgnore
    public String getDetails() {
        // Wrap the description to fit within 47 characters per line
        String formattedDescription = Utils.wrapText(this.prodDesc, 47);

        // Split the wrapped description into lines
        String[] descriptionLines = formattedDescription.split("\n");

        // Build the formatted description with proper alignment
        StringBuilder descriptionBuilder = new StringBuilder();
        for (String line : descriptionLines) {
            descriptionBuilder.append(String.format("│ %-47s │\n", line));
        }

        User currentUser = UserMenu.getCurrentUser();
        boolean isAdmin = currentUser instanceof Admin;

        // Format the product details
        StringBuilder details = new StringBuilder();
        details.append(String.format("│ Product ID     : %-30s │\n", this.prodID));
        details.append(String.format("│ Product Name   : %-30s │\n", this.prodName));
        details.append(String.format("│                                                 │\n"));
        details.append(String.format("│ Description:                                    │\n"));
        details.append(descriptionBuilder.toString().trim()).append("\n");
        details.append(String.format("│                                                 │\n"));
        if (isAdmin) {
            // Admin sees both unit price and selling price
            details.append(String.format("│ Unit Price     : RM%-28.2f │\n", this.unitPrice));
        }
        // Both admin and customer see the selling price
        details.append(String.format("│ Selling Price  : RM%-28.2f │\n", this.sellingPrice));
        details.append(String.format("│ Stock          : %-30d │", this.stock));

        return details.toString();

        // // Return the formatted product details
        // return String.format("""
        //                     │ Product ID: %-35s │
        //                     │ Product Name: %-33s │
        //                     │                                                 │
        //                     │ Product Description:                            │
        //                     %s
        //                     │                                                 │
        //                     │ Unit Price: $%-34.2f │
        //                     │ Selling Price: $%-31.2f │
        //                     │ Stock: %-40d │""",
        //     this.prodID,
        //     this.prodName,
        //     descriptionBuilder.toString().trim(), // Add the formatted description
        //     this.unitPrice,
        //     this.sellingPrice,
        //     this.stock
        // );
    }

    @JsonIgnore
    public abstract String getProductType();
}

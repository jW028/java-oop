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
    private String prodID;
    private String prodName;
    private String prodDesc;
    private double unitPrice;
    private double sellingPrice;
    private int stock;

    
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

    @JsonIgnore
    public String getDetails() {
        return "Product ID: " + this.prodID + "\n" +
               "Product Name: " + this.prodName + "\n" +
               "Product Description: " + this.prodDesc + "\n" +
               "Unit Price: $" + this.unitPrice + "\n" +
               "Selling Price: $" + this.sellingPrice + "\n" +
               "Stock: " + this.stock;
    }

    @JsonIgnore
    public abstract String getProductType();
}

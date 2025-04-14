package org.example;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Product {
    private String prodID;
    private String prodName;
    private String prodDesc;
    private Category category;
    private double unitPrice;
    private int stock;

    public Product() {}

    public Product(String prodID, String prodName, String prodDesc, Category category, double unitPrice, int stock) {
        this.prodID = prodID;
        this.prodName = prodName;
        this.prodDesc = prodDesc;
        this.category = category;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    public void updateStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
            System.out.println(prodName + " stock updated: " + stock);
        } else {
            System.out.println("Invalid quantity");
        }
    }

    @JsonIgnore
    public String getDetails() {
        return "Product Name: " + prodName + "\nCategory: " + category.getCategoryName() +
                "\n" + prodDesc + "\nPrice: $" + unitPrice + "\nStock Left: "
                + stock;
    }

    public String getProdID() {
        return prodID;
    }

    public String getProdName() {
        return prodName;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public Category getCategory() {
        return category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }


    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }



    
}
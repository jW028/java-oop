package org.example;

public class Product {
    private String prodID;
    private String prodName;
    private String prodDesc;
    private Category category;
    private double unitPrice;
    private int stock;

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

    public String getDetails() {
        return "Product Name: " + prodName + "\nCategory: " + category.getCategoryName() +
                "\n" + prodDesc + "\nPrice: $" + unitPrice + "\nStock Left: "
                + stock;
    }

    public String getProdName() {
        return prodName;
    }

    public Category getCategory() {
        return category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
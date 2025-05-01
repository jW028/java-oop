package com.gsports.java.oop;

public class CartItem {
    private Product product;
    private int quantity;
    private double subtotal;

    public CartItem() {}

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        setSubtotal();
    }

    public void setSubtotal() {
        this.subtotal = product.getSellingPrice() * quantity;
    }


    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        setSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setProduct(Product product) {
        this.product = product;
        setSubtotal();
    }

    @Override
    public String toString() {
        return quantity + " x " + product.getProdName() + " - $" + String.format("%.2f", subtotal);
    }
}

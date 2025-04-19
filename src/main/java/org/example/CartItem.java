package org.example;

public class CartItem {
    private Product product;
    private int quantity;
    private double subtotal;

    // Add a default no-args constructor for Jackson deserialization
    public CartItem() {
        // Default constructor required for Jackson
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        updateSubtotal();
    }

    private void updateSubtotal() {
        this.subtotal = product.getUnitPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }
    
    @Override
    public String toString() {
        return quantity + " x " + product.getProdName() + " - $" + String.format("%.2f", subtotal);
    }
}
package org.example;

import java.util.Arrays;
import java.util.Comparator;

public class Productlisting {
    private Product[] products;

    public Productlisting(Product[] products) {
        this.products = products;
    }

    public void displayProducts() {
        if (products.length == 0) {
            System.out.println("No products available.");
        } else {
            System.out.println("Available Products: ");
            for (Product product : products) {
                System.out.println("- " + product.getDetails());
            }
        }
    }

    public Product[] searchByName(String name) {
        return Arrays.stream(products)
                .filter(p -> p.getProdName().equalsIgnoreCase(name))
                .toArray(Product[]::new);
    }

    public Product[] searchByCategory(String categoryName) {
        return Arrays.stream(products)
                .filter(p -> p.getCategory().getCategoryName().equalsIgnoreCase(categoryName))
                .toArray(Product[]::new);
    }

    public void sortProductsByPrice() {
        Arrays.sort(products, Comparator.comparing(Product::getUnitPrice));
        System.out.println("Products sorted by price.");
    }
}
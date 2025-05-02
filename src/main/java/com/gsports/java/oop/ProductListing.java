package com.gsports.java.oop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ProductListing {
    private List<Product> products;

    public ProductListing(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Product> sortProductsByPrice(boolean ascending) {
        List<Product> sortedProducts = new ArrayList<>(products);
        if (ascending) {
            sortedProducts.sort(Comparator.comparing(Product::getSellingPrice));
            return sortedProducts;
        } else {
            sortedProducts.sort(Comparator.comparing(Product::getSellingPrice).reversed());
            return sortedProducts;
        }
    }

    public List<Product> sortProductsByName() {
        List<Product> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(Comparator.comparing(Product::getProdName));
        return sortedProducts;
    }

    public List<Product> sortProductsByCategory() {
        List<Product> sortedProducts = new ArrayList<>(products);

        sortedProducts.sort((p1, p2) -> {
            Map<String, Integer> categoryOrder = Map.of(
                "Laptop", 1,
                "Mouse", 2,
                "Accessory", 3
            );

            Integer order1 = categoryOrder.getOrDefault(p1.getProductType(), Integer.MAX_VALUE);
            Integer order2 = categoryOrder.getOrDefault(p2.getProductType(), Integer.MAX_VALUE);
            return order1.compareTo(order2);
        });

        return sortedProducts;
    }

    public List<Product> searchProductByName(String name) {
        List<Product> foundProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getProdName().toLowerCase().contains(name.toLowerCase())){
                foundProducts.add(product);
            }
        }
        return foundProducts;
    }

    // sales report


}

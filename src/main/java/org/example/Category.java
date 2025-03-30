package org.example;

public class Category {
    private String categoryID;
    private String categoryName;
    private String description;

    public Category(String categoryID, String categoryName, String description) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
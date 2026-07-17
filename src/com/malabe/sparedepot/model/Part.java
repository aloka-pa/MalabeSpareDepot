package com.malabe.sparedepot.model;

import java.time.LocalDate;

public class Part {
    private String code;
    private String name;
    private String brand;
    private double price;
    private int quantity;
    private String category;
    private LocalDate dateAdded;
    private String imageFile;

    public Part(String code, String name, String brand, double price, int quantity, String category, LocalDate dateAdded, String imageFile) {
        this.code = code;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.dateAdded = dateAdded;
        this.imageFile = imageFile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public boolean isLowStock(int threshold) {
        return quantity < threshold;
    }

    public String getCategoryKey() {
        if (category == null) {
            return "";
        }
        return category.trim().toUpperCase();
    }

    public Part copy() {
        return new Part(code, name, brand, price, quantity, category, dateAdded, imageFile);
    }
}

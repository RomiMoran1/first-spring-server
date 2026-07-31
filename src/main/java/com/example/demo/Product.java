package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // השם לא יכול להיות ריק או להכיל רק רווחים
    @NotBlank(message = "Title cannot be empty")
    private String title;

    // המחיר חייב להיות לפחות 0.1 (לא שלילי ולא אפס)
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private double price;

    // בנאי ריק (חובה בשביל JPA)
    public Product() {}

    public Product(String title, double price) {
        this.title = title;
        this.price = price;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    // ... שדות קיימים (id, name, price) ...

    @ManyToOne
    private Category category;

    // אל תשכח להוסיף Getter ו-Setter עבור category:
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
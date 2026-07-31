package com.example.demo;

public record ProductDTO(
    Long id,
    String title,
    Double price,
    String categoryName // במקום להחזיר את כל אובייקט הקטגוריה, מחזירים רק את שמה!
) {}
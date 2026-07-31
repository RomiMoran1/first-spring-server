package com.example.demo;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,           // למשל: 404, 400
    String error,          // למשל: "Not Found", "Bad Request"
    String message,        // ההודעה הברורה שלנו (למשל: "Product with ID 5 was not found")
    LocalDateTime timestamp // מתי השגיאה קרתה
) {}
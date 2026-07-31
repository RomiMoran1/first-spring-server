package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // הפונקציה הזו מופעלת אוטומטית כש-@Valid נכשל!
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();

        // עוברים על כל השדות שנכשלו באימות ואוספים את ההודעות שלנו
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        // מחזירים תשובה מותאמת אישית עם קוד 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    // הפונקציה הזו מופעלת אוטומטית כשאנחנו זורקים ResourceNotFoundException!
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
}
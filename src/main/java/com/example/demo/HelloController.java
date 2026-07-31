package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
public class HelloController {

    @Autowired
    private ProductRepository productRepository;

    // 1. שליפת כל המוצרים מה-Database (GET)
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. הוספת מוצר חדש ל-Database (POST)
    @PostMapping("/products")
    public ResponseEntity<String> addProduct(@Valid @RequestBody Product newProduct) {
        Product savedProduct = productRepository.save(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body("Product '" + savedProduct.getTitle() + "' saved to DB with ID: " + savedProduct.getId());
    }

    // 3. מחיקת מוצר לפי ID מתוך ה-Database (DELETE)
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product with ID " + id + " was deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Product with ID " + id + " was not found.");
        }
    }

    // 4. עדכון מחיר מוצר לפי ID ב-Database (PUT)
   // 4. עדכון מחיר מוצר לפי ID ב-Database (PUT)
    @PutMapping("/products/{id}")
    public ResponseEntity<String> updateProductPrice(@PathVariable Long id, @RequestBody Product updatedProduct) {
    
    // אם המוצר קיים - נחזיר אותו. אם לא - נזרוק שגיאה בשורה אחת!
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " was not found."));

    product.setPrice(updatedProduct.getPrice());
    productRepository.save(product);
    
    return ResponseEntity.ok("Product ID " + id + " price updated to: " + updatedProduct.getPrice());
}

    // 5. חיפוש מוצר לפי שם (GET)
    @GetMapping("/products/search/title/{title}")
    public ResponseEntity<Product> getProductByTitle(@PathVariable String title) {
        Optional<Product> product = productRepository.findByTitle(title);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 6. חיפוש מוצרים שהמחיר שלהם נמוך מסכום מסוים (GET)
    @GetMapping("/products/search/cheap/{maxPrice}")
    public List<Product> getCheapProducts(@PathVariable double maxPrice) {
        return productRepository.findByPriceLessThan(maxPrice);
    }
}
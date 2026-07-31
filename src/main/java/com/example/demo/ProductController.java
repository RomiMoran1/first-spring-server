package com.example.demo;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // עכשיו ה-Controller מחובר רק ל-Service!
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. שליפת כל המוצרים
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    // 2. שליפת מוצר לפי ID
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 3. יצירת מוצר חדש
    @PostMapping
    public ProductDTO createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }

    // 4. חיפוש לפי מחיר מקסימלי
    @GetMapping("/cheap")
    public List<ProductDTO> getCheapProducts(@RequestParam double maxPrice) {
        return productService.getCheapProducts(maxPrice);
    }

    // 5. חיפוש לפי מילת מפתח
    @GetMapping("/search")
    public List<ProductDTO> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // 6. עדכון מוצר קיים
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @Valid @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    // 7. מחיקת מוצר
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Product with ID " + id + " was successfully deleted";
    }
    // 8. שליפת מוצרים בחלוקה לעמודים ומיון
// GET /api/products/paged?page=0&size=5&sortBy=price&direction=asc
@GetMapping("/paged")
public Page<ProductDTO> getProductsPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {
    
    return productService.getProductsWithPaging(page, size, sortBy, direction);
}
    
}
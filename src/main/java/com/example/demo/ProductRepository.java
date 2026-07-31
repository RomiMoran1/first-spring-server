package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Spring מבינה מזה: SELECT * FROM product WHERE title = ?
    Optional<Product> findByTitle(String title);

    // 2. Spring מבינה מזה: SELECT * FROM product WHERE price < ?
    List<Product> findByPriceLessThan(double maxPrice);
    List<Product> findByTitleContainingIgnoreCase(String keyword);
    
}
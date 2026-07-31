package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository; // דאטאבייס מדומה (Mock)

    @InjectMocks
    private ProductService productService; // ה-Service האמיתי שנבדוק

    @Test
    public void getProductById_WhenProductExists_ShouldReturnProductDTO() {
        // 1. Arrange - יצירת מוצר דמה לפי הקונסטרקטור של Product שלך
        Product fakeProduct = new Product("Laptop", 1000.0);

        // אומרים ל-Mock: כשקוראים ל-findById(1L), תחזיר את המוצר הדמה
        when(productRepository.findById(1L)).thenReturn(Optional.of(fakeProduct));

        // 2. Act - הפעלת המתודה ב-Service
        ProductDTO result = productService.getProductById(1L);

        // 3. Assert - בדיקת תוצאות (שימוש ב-title() ו-price() של ה-Record)
        assertNotNull(result);
        assertEquals("Laptop", result.title());
        assertEquals(1000.0, result.price());

        // וידוא שהקריאה ל-findById התבצעה בדיוק פעם אחת
        verify(productRepository, times(1)).findById(1L);
    }
}
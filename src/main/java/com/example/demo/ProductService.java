// הגדרת החבילה (Package) שבה נמצא הקובץ בתוך הפרויקט
package com.example.demo;

// ייבוא מחלקת List לעבודה עם רשימות בג'אווה
import java.util.List;
// ייבוא האנוטציה Service של Spring
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// האנוטציה מסמנת ל-Spring שמחלקה זו היא Service (שכבת הלוגיקה העסקית)
// ספרינג ינהל את המחלקה הזו אוטומטית כמחלקה מרכזית (Bean) במערכת
@Service
public class ProductService {

    // הגדרת משתנה פרטי עבור ה-Repository שדרכו נדבר עם מסד הנתונים
    // ה-final מבטיח שמינוי המשתנה יתרחש פעם אחת בלבד ולא ישתנה
    private final ProductRepository productRepository;

    // הזרקת תלויות (Dependency Injection) דרך ה-Constructor
    // ספרינג מעביר לכאן אוטומטית מופע של ProductRepository כשהמערכת עולה
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // מתודת עזר פרטית להמרת אובייקט מסוג Entity (מהדאטאבייס) לאובייקט מסוג DTO (החוצה ללקוח)
    private ProductDTO convertToDto(Product product) {
        // בדיקת בטיחות: אם יש מוצר משויך לקטגוריה – נביא את שמה, אחרת נחזיר null מבוקר
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : null;
        
        // יצירת מופע חדש של ProductDTO והחזרתו
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                categoryName
        );
    }

    // מתודה לשליפת כל המוצרים מהדאטאבייס
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll() // שולף את כל הישויות (Entities) מהדאטאבייס
                .stream()                   // הופך את הרשימה ל-Stream כדי לאפשר עיבוד נתונים מתקדם
                .map(this::convertToDto)    // מעביר כל מוצר ברשימה דרך מתודת ההמרה ל-DTO
                .toList();                  // אוסף את התוצאות המומרות בחזרה לרשימה חדשה
    }

    // מתודה לשליפת מוצר בודד לפי ה-ID שלו
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id) // מחפש בדאטאבייס (מחזיר אובייקט מסוג Optional)
                // אם ה-ID לא קיים, זורק אקספשן מותאם אישית שייתפס ב-GlobalExceptionHandler ויחזיר 404
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " was not found"));
        
        // המרת המוצר שנמצא ל-DTO והחזרתו
        return convertToDto(product);
    }

    // מתודה ליצירת מוצר חדש בדאטאבייס
    public ProductDTO createProduct(Product product) {
        Product savedProduct = productRepository.save(product); // שומר את המוצר החדש בדאטאבייס
        return convertToDto(savedProduct);                      // ממיר את המוצר השמור ל-DTO ומחזיר ללקוח
    }

    // מתודה לשליפת מוצרים שמחירם נמוך ממחיר מקסימלי מסוים
    public List<ProductDTO> getCheapProducts(double maxPrice) {
        return productRepository.findByPriceLessThan(maxPrice) // שאילתה מותאמת אישית מול ה-Repository
                .stream()                                      // המרה ל-Stream
                .map(this::convertToDto)                       // המרת כל Entity ברשימה ל-DTO
                .toList();                                     // איסוף מחדש לרשימה
    }

    // מתודה לחיפוש מוצרים לפי מילת מפתח בשם המוצר
    public List<ProductDTO> searchProducts(String keyword) {
        return productRepository.findByTitleContainingIgnoreCase(keyword) // שאילתה המחפשת טקסט תוך התעלמות מ-Capital/Small letters
                .stream()                                                  // המרה ל-Stream
                .map(this::convertToDto)                                   // המרת כל Entity ל-DTO
                .toList();                                                 // איסוף מחדש לרשימה
    }

    // מתודה לעדכון מוצר קיים לפי ID
    public ProductDTO updateProduct(Long id, Product updatedProduct) {
        // בודק קודם שהמוצר שאנחנו רוצים לעדכן בכלל קיים בדאטאבייס, ואם לא – זורק 404
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " was not found"));

        // עדכון השדות של המוצר הקיים בנתונים החדשים שהתקבלו
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setPrice(updatedProduct.getPrice());

        // אם נשלחה קטגוריה חדשה לעדכון – מעדכן גם אותה
        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }

        // שמירת האובייקט המעודכן בדאטאבייס (Spring JPA מבין שזה עדכון ולא יצירה כי יש לו ID)
        Product savedProduct = productRepository.save(existingProduct);
        return convertToDto(savedProduct); // המרה ל-DTO והחזרה
    }

    // מתודה למחיקת מוצר לפי ID
    public void deleteProduct(Long id) {
        // בודק אם ה-ID קיים בדאטאבייס לפני תהליך המחיקה
        if (!productRepository.existsById(id)) {
            // אם המוצר לא קיים, זורק אקספשן במקום שתיזרק שגיאת דאטאבייס מגעילה
            throw new ResourceNotFoundException("Cannot delete. Product with ID " + id + " was not found");
        }
        
        // מוחק את המוצר מהדאטאבייס לפי ה-ID
        productRepository.deleteById(id);
    }
    // שליפת מוצרים בחלוקה לעמודים ומיון
public Page<ProductDTO> getProductsWithPaging(int page, int size, String sortBy, String direction) {
    // 1. הגדרת כיוון המיון (עולה או יורד)
    Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();

    // 2. יצירת אובייקט שמגדיר את מספר העמוד, גודל העמוד והמיון
    Pageable pageable = PageRequest.of(page, size, sort);

    // 3. שליפת העמוד מהדאטאבייס והמרתו ל-ProductDTO
    return productRepository.findAll(pageable)
            .map(this::convertToDto);
}
    
}
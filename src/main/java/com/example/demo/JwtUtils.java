package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // מפתח סודי מוצפן לאימות החתימה (במערכת אמיתית שומרים אותו בקובץ הגדרות מאובטח)
    private final String SECRET_STRING = "MySuperSecretKeyForSpringSecurityJwtTokenGeneration123456!";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // זמן תפוגה של ה-Token במילי-שניות (24 שעות)
    private final long EXPIRATION_TIME = 86400000;

    // 1. יצירת Token חדש עבור משתמש
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // מציבים את שם המשתמש בתוך ה-Payload
                .issuedAt(new Date()) // תאריך הנפקת הטוקן
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // תאריך תפוגה
                .signWith(key) // חתימה דיגיטלית באמצעות המפתח הסודי שלנו
                .compact(); // דחיסה למחרוזת טקסט אחת
    }

    // 2. חילוץ שם המשתמש מתוך ה-Token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key) // בדיקה שהחתימה תואמת למפתח הסודי שלנו
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // 3. אימות תקינות ה-Token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true; // הטוקן תקין, חתום כראוי ובתוקף
        } catch (JwtException | IllegalArgumentException e) {
            // ה-Token מזויף, שגוי או שפג תוקפו
            return false;
        }
    }
}
package com.example.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    // הזרקת תלויות דרך Constructor
 // הזרקת תלויות עם @Lazy על userDetailsService כדי למנוע תלות מעגלית
    public JwtFilter(JwtUtils jwtUtils, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. חילוץ הדר ה-Authorization מתוך הבקשה
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. בדיקה שההדר קיים ומתחיל במילה "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // גזירת 7 התווים הראשונים ("Bearer ")
            if (jwtUtils.validateToken(token)) { // בדיקה שהטוקן תקין
                username = jwtUtils.getUsernameFromToken(token); // חילוץ שם המשתמש
            }
        }

        // 3. אם חולץ שם משתמש ואין עדיין אימות פעיל בקונטקסט של Spring Security
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // טעינת פרטי המשתמש והתפקידים שלו מ-UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // יצירת אובייקט אימות של Spring Security
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // עדכון ה-SecurityContext שהמשתמש מאושר לשרת!
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 4. העברת הבקשה לפילטר הבא בשרשרת (או ל-Controller)
        filterChain.doFilter(request, response);
    }
}
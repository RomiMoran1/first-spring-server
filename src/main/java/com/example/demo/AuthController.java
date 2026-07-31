package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // 1. בדיקת תקינות שם המשתמש והסיסמה מול Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(), 
                            authRequest.getPassword()
                    )
            );

            // 2. אם הפרטים נכונים - מייצרים JWT Token
            String token = jwtUtils.generateToken(authRequest.getUsername());

            // 3. החזרת ה-Token ללקוח בתוך אובייקט JSON
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {
            // אם שם המשתמש או הסיסמה שגויים - מחזירים שגיאת 401 Unauthorized
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }
}
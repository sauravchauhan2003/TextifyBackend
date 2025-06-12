package com.example.demo.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JWTUtil {
    private static final String SECRET = "skhdgfshvbawbdhsbfjwandheabfjndhabfjwandhabf";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));


    // Thread-safe shared ObjectMapper instance
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Generate JWT for a given user
    public static String generateKey(UserEntity user) {
        try {
            // Convert the user to a Map (or store individual fields if preferred)
            Map<String, Object> userMap = objectMapper.convertValue(user, Map.class);

            return Jwts.builder()
                    .claim("user", userMap)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 days
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    // Validate if token is well-formed and signed correctly
    public static boolean isValidToken(String token) {
        System.out.println(key);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract UserEntity from the JWT
    public static UserEntity extractUser(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object userObj = claims.get("user");

            if (userObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) userObj;
                return objectMapper.convertValue(userMap, UserEntity.class);
            }

            throw new RuntimeException("Invalid 'user' claim format in token");

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract user from JWT", e);
        }
    }
}

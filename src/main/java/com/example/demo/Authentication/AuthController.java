package com.example.demo.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
public class AuthController {
    @Autowired
    private UserRepository repository;
    @GetMapping("/register")
    public String register(@RequestHeader String username, @RequestHeader String password,@RequestHeader String email) {
        if (repository.findByUsername(username).isPresent()) {
            return "User already exists";
        }
        else if(repository.findByEmail(email).isPresent()){
            return "Email already in use";
        }
        else {
            String id = UUID.randomUUID().toString();
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setUserId(id);
            user.setUsername(username);
            user.setPassword(password);
            repository.save(user);
            return JWTUtil.generateKey(user);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }

            token = token.substring(7); // Strip "Bearer "
            if (JWTUtil.isValidToken(token)) {
                return ResponseEntity.ok().build(); // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token verification failed");
        }
    }
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestHeader String username, @RequestHeader String password) {
        var userOptional = repository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");
        }

        var user = userOptional.get();

        if (Objects.equals(user.getPassword(), password)) {
            return ResponseEntity.ok(JWTUtil.generateKey(user));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");
        }
    }
/// ihdudakfajhdeh

}

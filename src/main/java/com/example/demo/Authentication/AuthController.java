package com.example.demo.Authentication;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
public class AuthController {
    @Autowired
    private UserRepository repository;
    @PostMapping("/register")
    public String register(@RequestHeader String username, @RequestHeader String password, @RequestHeader String email, HttpServletResponse response) {
        if (repository.findByUsername(username).isPresent()) {
            response.setStatus(400);
            return "User already exists";
        }
        else if(repository.findByEmail(email).isPresent()){
            response.setStatus(400);
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
            response.setStatus(200);
            return JWTUtil.generateKey(user);
        }
    }

    @PostMapping("/verify")
    public void verify(@RequestHeader("Authorization") String token, HttpServletResponse response) throws IOException {
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        token = token.substring(7); // Strip "Bearer "

        if (JWTUtil.isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_OK); // 200
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
        }
    }
    @PostMapping("/login")
    public void login(
            @RequestHeader String username,
            @RequestHeader String password,
            HttpServletResponse response) throws IOException {

        var userOptional = repository.findByUsername(username);

        if (userOptional.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Credentials");
            return;
        }

        var user = userOptional.get();

        if (Objects.equals(user.getPassword(), password)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JWTUtil.generateKey(user));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Credentials");
        }
    }



}

package com.example.demo.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AuthController {
    @Autowired
    private UserRepository repository;
    @GetMapping("/register")
    String register(@RequestHeader String username, @RequestHeader String password){
        if(repository.findByUsername(username).isPresent()){
            return "User already exists";
        }
        else{
            String id= UUID.randomUUID().toString();
            UserEntity user=new UserEntity();
            user.setUserId(id);
            user.setUsername(username);
            user.setPassword(password);
            repository.save(user);
            return JWTUtil.generateKey(user);
        }
    }
}

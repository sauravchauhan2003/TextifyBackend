package com.example.TextifyBackend.Authentication;

import com.example.TextifyBackend.Repo.MyRepo;
import com.example.TextifyBackend.Repo.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private MyRepo myRepo;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody MyUser myUser){
        if(authService.useralreadyexists(myUser.getUsername())){
            return "Username already used";
        }
        else {
            myRepo.save(myUser);
            return authService.generateJWt(myUser);
        }

    }
}

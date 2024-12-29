package com.example.TextifyBackend.Repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PorfilePicController {
    @Autowired
    private ProfilePicRepo profilePicRepo;
    @PostMapping("/setprofilepic")
    public void setprofilepicture(@RequestBody ProfilePic profilePic){
        profilePicRepo.save(profilePic);
    }
    @GetMapping("/getprofilepic")
    public ProfilePic getProfilepic(String username){
        Optional<ProfilePic> profilePic=profilePicRepo.findByUsername(username);
        if(profilePic.isEmpty()){
            return null;
        }
        else return profilePic.get();
    }
}

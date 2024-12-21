package com.example.TextifyBackend.Authentication;

import com.example.TextifyBackend.Repo.MyRepo;
import com.example.TextifyBackend.Repo.MyUser;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class AuthService {
    private static final Key key= Keys.secretKeyFor(SignatureAlgorithm.HS256);
    @Autowired
    private MyRepo myRepo;

    public boolean useralreadyexists(String username){
        if(myRepo.findByUsername(username).isEmpty()){
            return false;
        }
        else return true;
    }
    public String generateJWt(MyUser myUser){
        Map<String, Object> claims = new HashMap<>();
        claims.put("email",myUser.getEmail());
        claims.put("password",myUser.getPassword());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(myUser.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean correctcredentials(MyUser myUser) {
        Optional<MyUser> a = myRepo.findByUsername(myUser.getUsername());
        if (a.isEmpty()) {
            return false;
        } else {
            if (myUser.getPassword().equals(a.get().getPassword())) {
                return true;
            }
            else return false;
        }


    }

    public boolean verifyJwt(String jwt){
       Claims claims= Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
       return true;
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public MyUser loadUserByUsername(String username)  {
        Optional<MyUser> user = myRepo.findByUsername(username);
        MyUser userEntity=new MyUser();
        if (!user.isEmpty()) {
            userEntity=user.get();
        }


        return userEntity;
    }

}

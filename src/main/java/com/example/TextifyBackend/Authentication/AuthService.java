package com.example.TextifyBackend.Authentication;

import com.example.TextifyBackend.Repo.MyRepo;
import com.example.TextifyBackend.Repo.MyUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
}

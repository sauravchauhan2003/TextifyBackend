package com.example.TextifyBackend.Authentication;

import com.example.TextifyBackend.Repo.MyRepo;
import com.example.TextifyBackend.Repo.MyUser;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
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
    public GoogleIdToken verifyGoogleToken(String token) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList("YOUR_CLIENT_ID"))
                .build();

        try {
            return verifier.verify(token);
        } catch (Exception e) {
            return null;
        }
    }
    public MyUser extractUserDetailsFromToken(String token) {
        GoogleIdToken googleIdToken = verifyGoogleToken(token);
        if (googleIdToken == null) {
            return null; // Return null or throw an exception if verification fails
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String username = (String) payload.get("name"); // You can use 'name' or any other field from payload

        // Create and populate the MyUser object
        MyUser user = new MyUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword("");  // Set password to empty or generate a default password

        return user;
    }

}

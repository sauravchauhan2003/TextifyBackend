package com.example.TextifyBackend.Authentication;

import com.example.TextifyBackend.Repo.MyRepo;
import com.example.TextifyBackend.Repo.MyUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;
import java.util.Collections;
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private AuthService authService;

    @Autowired
    private MyRepo myRepo;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth_header=request.getHeader("Authorization");
        String token =null;
        String Username=null;
        if(auth_header!=null && auth_header.startsWith("Bearer")){
            token=auth_header.substring(7);
            Username=authService.extractUsername(token);
        }
        if(Username!= null && SecurityContextHolder.getContext().getAuthentication()==null){
            if(authService.validateToken(token)){
                MyUser myUser= authService.loadUserByUsername(Username);
                if(myUser.getUsername()!=null){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            myUser, // Principal
                            null, // No need for credentials as user is already authenticated
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Authorities
                    );

                    // Set the Authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}

package com.example.TextifyBackend.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyRepo extends JpaRepository<MyUser,Integer> {
    public Optional<MyUser> findByUsername(String username);
}

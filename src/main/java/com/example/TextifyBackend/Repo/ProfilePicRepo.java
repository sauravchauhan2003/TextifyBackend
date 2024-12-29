package com.example.TextifyBackend.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePicRepo extends JpaRepository<ProfilePic,Integer> {
    public Optional<ProfilePic> findByUsername(String username);
}

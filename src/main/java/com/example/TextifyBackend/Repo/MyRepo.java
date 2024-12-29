package com.example.TextifyBackend.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MyRepo extends JpaRepository<MyUser,Integer> {
    public Optional<MyUser> findByUsername(String username);
    @Query("SELECT u.username FROM MyUser u WHERE u.username <> :excludedUsername")
    List<String> findAllUsernamesExcept(@Param("excludedUsername") String excludedUsername);
}

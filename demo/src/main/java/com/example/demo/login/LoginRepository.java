package com.example.demo.login;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByEmail(String userEmail);
    boolean existsByEmail(String useremail);
    boolean existsByName(String username);
}
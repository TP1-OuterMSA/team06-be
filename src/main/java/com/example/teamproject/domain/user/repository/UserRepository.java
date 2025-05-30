package com.example.teamproject.domain.user.repository;

import com.example.teamproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUserId(Long id);
    boolean existsByUsername(String username);
}

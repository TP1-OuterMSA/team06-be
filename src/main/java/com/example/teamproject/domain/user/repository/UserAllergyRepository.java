package com.example.teamproject.domain.user.repository;

import com.example.teamproject.domain.user.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {
    // Custom query methods can be defined here if needed
}

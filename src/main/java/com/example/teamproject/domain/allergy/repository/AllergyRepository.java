package com.example.teamproject.domain.allergy.repository;

import com.example.teamproject.domain.allergy.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    Optional<Allergy> findByName(String name);
}

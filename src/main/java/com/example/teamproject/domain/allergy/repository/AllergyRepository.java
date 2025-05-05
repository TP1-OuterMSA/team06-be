package com.example.teamproject.domain.allergy.repository;

import com.example.teamproject.domain.allergy.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
}

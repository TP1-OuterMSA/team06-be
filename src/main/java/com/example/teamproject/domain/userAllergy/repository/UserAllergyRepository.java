package com.example.teamproject.domain.userAllergy.repository;

import com.example.teamproject.domain.userAllergy.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {
    // Custom query methods can be defined here if needed

    List<UserAllergy> findByUserId(Long id);
    void deleteByUserId(Long id);
    void deleteAllByUserIdAndAllergyIdIn(Long userId, List<Long> allergyIds);

}

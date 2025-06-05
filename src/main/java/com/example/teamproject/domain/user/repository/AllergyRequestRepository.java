package com.example.teamproject.domain.user.repository;

import com.example.teamproject.domain.user.entity.AllergyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyRequestRepository extends JpaRepository<AllergyRequest, Long> {
    List<AllergyRequest> findByStatus(AllergyRequest.Status status);
}

package com.example.teamproject.domain.user.repository;

import com.example.teamproject.domain.user.entity.PromotionRequest;
import com.example.teamproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PromotionRequestRepository extends JpaRepository<PromotionRequest, Long> {
    List<PromotionRequest> findByStatus(PromotionRequest.Status status);
    List<PromotionRequest> findByUserOrderByRequestedAtDesc(User user);

}
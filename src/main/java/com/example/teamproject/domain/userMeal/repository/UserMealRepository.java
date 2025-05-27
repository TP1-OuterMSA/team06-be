package com.example.teamproject.domain.userMeal.repository;

import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.userMeal.entity.UserMeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMealRepository extends JpaRepository<UserMeal, Long> {
    List<UserMeal> findByUserId(Long userId);
    boolean existsByUserAndMeal(User user, Meal meal);
}

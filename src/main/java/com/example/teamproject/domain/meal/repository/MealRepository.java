package com.example.teamproject.domain.meal.repository;


import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.mealSchedule.entity.MealSchedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByName(String name);
}

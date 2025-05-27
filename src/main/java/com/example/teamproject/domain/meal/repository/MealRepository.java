package com.example.teamproject.domain.meal.repository;


import com.example.teamproject.domain.meal.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
}

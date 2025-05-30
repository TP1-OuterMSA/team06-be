package com.example.teamproject.domain.mealSchedule.repository;

import com.example.teamproject.domain.mealSchedule.entity.MealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealScheduleRepository extends JpaRepository<MealSchedule, Long> {
    Optional<MealSchedule> findByMealTypeAndDay(String mealType, String day);
    List<MealSchedule> findAllByDayIn(List<String> days);

    boolean existsByMealTypeAndDay(String mealType, String day);
}

package com.example.teamproject.domain.mealScheduleItem.repository;

import com.example.teamproject.domain.mealSchedule.entity.MealSchedule;
import com.example.teamproject.domain.mealScheduleItem.entity.MealScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealScheduleItemRepository extends JpaRepository<MealScheduleItem, Long> {
    void deleteAllByMealSchedule(MealSchedule schedule);
    List<MealScheduleItem> findAllByMealScheduleOrderByOrderNum(MealSchedule schedule);
}

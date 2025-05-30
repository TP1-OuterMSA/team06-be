package com.example.teamproject.domain.mealScheduleItem.entity;

import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.mealSchedule.entity.MealSchedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    private MealSchedule mealSchedule;

    private Integer orderNum;
}

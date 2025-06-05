package com.example.teamproject.domain.meal.entity;

import com.example.teamproject.domain.mealSchedule.entity.MealSchedule;
import com.example.teamproject.domain.mealScheduleItem.entity.MealScheduleItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;

    @OneToMany(mappedBy = "meal")
    private List<MealScheduleItem> scheduleItems = new ArrayList<>();

}

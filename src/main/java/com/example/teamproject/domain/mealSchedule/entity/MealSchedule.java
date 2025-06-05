package com.example.teamproject.domain.mealSchedule.entity;

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
public class MealSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String mealType;

    @OneToMany(mappedBy = "mealSchedule")
    private List<MealScheduleItem> scheduleItems = new ArrayList<>();
}

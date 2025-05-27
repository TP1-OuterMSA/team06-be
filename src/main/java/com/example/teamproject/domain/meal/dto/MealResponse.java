package com.example.teamproject.domain.meal.dto;

import com.example.teamproject.domain.meal.entity.Meal;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MealResponse {
    private Long id;
    private String name;
    private String category;
}

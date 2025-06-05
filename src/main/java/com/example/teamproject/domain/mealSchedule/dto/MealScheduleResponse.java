package com.example.teamproject.domain.mealSchedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MealScheduleResponse {
    private String day;
    private String mealType;
    private List<String> menus;
}

package com.example.teamproject.domain.userMeal.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserMealRequest {
    private List<Long> meals;
}

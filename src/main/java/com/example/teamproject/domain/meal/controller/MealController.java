package com.example.teamproject.domain.meal.controller;

import com.example.teamproject.domain.meal.dto.MealResponse;
import com.example.teamproject.domain.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/team6/meal")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    // Meal 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<MealResponse>> getMealList() {
        List<MealResponse> mealList = mealService.getMealList();
        return ResponseEntity.ok(mealList);
    }

}

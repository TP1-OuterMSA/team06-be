package com.example.teamproject.domain.userMeal.controller;

import com.example.teamproject.domain.meal.dto.MealResponse;
import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.userAllergy.dto.request.UpdateUserAllergyDto;
import com.example.teamproject.domain.userMeal.dto.UserMealRequest;
import com.example.teamproject.domain.userMeal.service.UserMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team6/user/meal/favorites")
public class UserMealController {

    private final UserMealService userMealService;

    // 좋아하는 메뉴 조회
    @GetMapping
    public ResponseEntity<List<MealResponse>> getFavoriteMeals(@RequestParam String username) {
        return ResponseEntity.ok(userMealService.getFavoriteMeals(username));
    }

    // 좋아하는 메뉴 추가
    @PutMapping
    public void updateUserAllergy(@RequestBody UserMealRequest userMealRequest) {
//        userMealService.
    }

}

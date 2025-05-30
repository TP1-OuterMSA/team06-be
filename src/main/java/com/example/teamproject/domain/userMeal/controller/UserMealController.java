package com.example.teamproject.domain.userMeal.controller;

import com.example.teamproject.domain.meal.dto.MealResponse;
import com.example.teamproject.domain.userMeal.dto.UserMealRequest;
import com.example.teamproject.domain.userMeal.service.UserMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/team6/user/meal")
public class UserMealController {

    private final UserMealService userMealService;

    // 좋아하는 메뉴 조회
    @GetMapping("/favorite")
    public ResponseEntity<List<MealResponse>> getFavoriteMeals(@RequestHeader("userId") Long userId) {
        return ResponseEntity.ok(userMealService.getFavoriteMeals(userId));
    }

    // 좋아하는 메뉴 추가
    @PostMapping("/favorite")
    public ResponseEntity<String> addFavoriteMeals(@RequestHeader("userId") Long userId, @RequestBody UserMealRequest userMealRequest) {
        userMealService.replaceFavoriteMeals(userId, userMealRequest);
        return ResponseEntity.ok("좋아하는 메뉴가 반영되었습니다.");
    }

}

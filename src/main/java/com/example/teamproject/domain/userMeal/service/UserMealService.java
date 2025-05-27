package com.example.teamproject.domain.userMeal.service;

import com.example.teamproject.domain.meal.dto.MealResponse;
import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.service.UserService;
import com.example.teamproject.domain.userMeal.entity.UserMeal;
import com.example.teamproject.domain.userMeal.repository.UserMealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMealService {

    private final UserMealRepository userMealRepository;
    private final UserService userService;

    public List<MealResponse> getFavoriteMeals(String username) {
        UserDto userDto = userService.getByUsername(username);
        if (userDto == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Long userId = userDto.getId();
        List<UserMeal> favoriteMeals = userMealRepository.findByUserId(userId);
        return favoriteMeals.stream()
                .map(UserMeal::getMeal)
                .map(meal -> MealResponse.builder()
                        .id(meal.getId())
                        .name(meal.getName())
                        .build())
                .toList();
    }
}

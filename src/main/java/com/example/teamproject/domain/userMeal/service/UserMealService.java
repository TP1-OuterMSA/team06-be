package com.example.teamproject.domain.userMeal.service;

import com.example.teamproject.domain.meal.dto.MealResponse;
import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.meal.service.MealService;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.service.UserService;
import com.example.teamproject.domain.userMeal.dto.UserMealRequest;
import com.example.teamproject.domain.userMeal.entity.UserMeal;
import com.example.teamproject.domain.userMeal.repository.UserMealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMealService {

    private final UserMealRepository userMealRepository;
    private final UserService userService;
    private final MealService mealService;

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
                        .category(meal.getCategory())
                        .build())
                .toList();
    }

    public void addFavoriteMeals(UserMealRequest userMealRequest) {
        Long userId = 3L;
        User user = userService.findById(userId);
        for (Long mealId : userMealRequest.getMeals()) {
            Meal meal = mealService.findById(mealId);
            boolean exists = userMealRepository.existsByUserAndMeal(user, meal);
            if (!exists) {
                UserMeal userMeal = UserMeal.builder()
                        .user(user)
                        .meal(meal)
                        .build();
                userMealRepository.save(userMeal);
            }
        }
    }
}

package com.example.teamproject.domain.meal.service;

import com.example.kafka_schemas.CategoryEvent;
import com.example.teamproject.domain.meal.dto.MealResponse;
import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.meal.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;

    public Meal findById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Meal ID: " + id));
    }

    public void saveMeal(CategoryEvent categoryEvent) {
        if(mealRepository.findByName(categoryEvent.getMealName()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 Meal 입니다: " + categoryEvent.getMealName());
        }
        Meal meal = Meal.builder()
                .name(categoryEvent.getMealName())
                .category(categoryEvent.getMealCategory())
                .build();
        mealRepository.save(meal);
    }

    public List<MealResponse> getMealList() {
        List<Meal> meals = mealRepository.findAll();
        return meals.stream()
                .map(meal -> MealResponse.builder()
                        .id(meal.getId())
                        .name(meal.getName())
                        .category(meal.getCategory())
                        .build())
                .toList();
    }
}

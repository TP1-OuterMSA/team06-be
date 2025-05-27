package com.example.teamproject.domain.meal.service;

import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.meal.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;

    public Meal findById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Meal ID: " + id));
    }

}

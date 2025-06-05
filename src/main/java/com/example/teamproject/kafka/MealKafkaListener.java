package com.example.teamproject.kafka;

import com.example.kafka_schemas.CategoryEvent;
import com.example.kafka_schemas.MealEvent;
import com.example.teamproject.domain.meal.service.MealService;
import com.example.teamproject.domain.mealSchedule.service.MealScheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MealKafkaListener {

    private final MealService mealService;
    private final MealScheduleService mealScheduleService;

    @KafkaListener(topics = "meal.web.crawler.updated", groupId = "team06-service")
    public void consumeMeal(MealEvent mealEvent) {
        mealScheduleService.saveMealSchedule(mealEvent);
    }

    @KafkaListener(topics = "meal.category.updated", groupId = "team06-service")
    public void consumeCategory(CategoryEvent categoryEvent) {
        mealService.saveMeal(categoryEvent);
    }

}

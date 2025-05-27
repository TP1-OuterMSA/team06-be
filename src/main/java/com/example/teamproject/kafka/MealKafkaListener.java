package com.example.teamproject.kafka;

import com.example.kafka_schemas.CategoryEvent;
import com.example.kafka_schemas.MealEvent;
import com.example.teamproject.domain.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MealKafkaListener {

    private final MealService mealService;

    @KafkaListener(topics = "meal.web.crawler.updated", groupId = "team06-service")
    public void consume(MealEvent eventMenu) {
        System.out.println("eventMenu = " + eventMenu.getMealContents());
    }

    @KafkaListener(topics = "meal.category.updated", groupId = "team06-service")
    public void consume(CategoryEvent categoryEvent) {
        mealService.saveMeal(categoryEvent);
    }

}

package com.example.teamproject.kafka;

import com.example.kafka_schemas.NutritionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MealDataListener {

    @KafkaListener(topics = "meal.web.crawler.updated", groupId = "team06-service")
    public void consume(NutritionEvent nutritionEvent) {
        System.out.println(nutritionEvent);
    }
}

package com.example.teamproject.kafka;

import com.example.kafka_schemas.EventMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MealDataListener {

    private final MealDataService mealDataService;

    @KafkaListener(topics = "meal.web.crawler.updated", groupId = "team06-service")
    public void consume(EventMenu eventMenu) {
        mealDataService.update(eventMenu);
        System.out.println("eventMenu = " + eventMenu.getEventTitle());
    }
}

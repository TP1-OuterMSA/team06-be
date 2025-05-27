package com.example.teamproject.kafka;

import com.example.kafka_schemas.EventMenu;
import com.example.kafka_schemas.MealEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealDataService {
    private MealEvent latestEvent;

    public void update(MealEvent eventMenu) {
        this.latestEvent = eventMenu;
    }

    public MealEvent getLatest() {
        return latestEvent;
    }
}
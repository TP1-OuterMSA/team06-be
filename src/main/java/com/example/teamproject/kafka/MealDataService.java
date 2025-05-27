package com.example.teamproject.kafka;

import com.example.kafka_schemas.EventMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealDataService {
    private EventMenu latestEvent;

    public void update(EventMenu eventMenu) {
        this.latestEvent = eventMenu;
    }

    public EventMenu getLatest() {
        return latestEvent;
    }
}
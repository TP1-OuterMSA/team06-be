package com.example.teamproject.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/team6/kafka")
public class MealDataController {

    private final MealDataService mealDataService;

    @GetMapping("/event-menu")
    public void getEventMenu(){
        System.out.println("mealDataService.getLatest() = " + mealDataService.getLatest());
    }
}

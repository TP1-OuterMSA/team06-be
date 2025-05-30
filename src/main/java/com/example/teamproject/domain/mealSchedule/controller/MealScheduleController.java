package com.example.teamproject.domain.mealSchedule.controller;

import com.example.kafka_schemas.MealEvent;
import com.example.teamproject.domain.mealSchedule.dto.MealScheduleResponse;
import com.example.teamproject.domain.mealSchedule.service.MealScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/team6/meal/schedule")
@RequiredArgsConstructor
public class MealScheduleController {

    private final MealScheduleService mealScheduleService;

    @GetMapping("/week")
    public ResponseEntity<List<MealScheduleResponse>> getWeeklyMeals() {
        return ResponseEntity.ok(mealScheduleService.getWeeklyMeals());
    }

    @GetMapping("/day")
    public ResponseEntity<MealScheduleResponse> getDailyMeal(
            @RequestParam String day,
            @RequestParam String mealType
    ) {
        return ResponseEntity.ok(mealScheduleService.getDailyMeal(day, mealType));
    }

    @PostMapping("/update")
    public ResponseEntity<Void> saveMeal(@RequestBody MealEvent mealEvent) {
        mealScheduleService.saveMealSchedule(mealEvent);
        return ResponseEntity.ok().build();
    }

}

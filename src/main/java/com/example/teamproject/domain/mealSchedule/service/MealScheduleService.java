package com.example.teamproject.domain.mealSchedule.service;

import com.example.kafka_schemas.MealEvent;
import com.example.teamproject.domain.meal.entity.Meal;
import com.example.teamproject.domain.meal.repository.MealRepository;
import com.example.teamproject.domain.mealSchedule.dto.MealScheduleResponse;
import com.example.teamproject.domain.mealSchedule.entity.MealSchedule;
import com.example.teamproject.domain.mealSchedule.repository.MealScheduleRepository;
import com.example.teamproject.domain.mealScheduleItem.entity.MealScheduleItem;
import com.example.teamproject.domain.mealScheduleItem.repository.MealScheduleItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealScheduleService {

    private final MealScheduleRepository mealScheduleRepository;
    private final MealRepository mealRepository;
    private final MealScheduleItemRepository itemRepository;

    public void saveMealSchedule(MealEvent event) {
        // 1. 요일 계산
        LocalDate date = LocalDate.parse(event.getDate());
        String day = getKoreanDayOfWeek(date.getDayOfWeek());
        String mealType = event.getMealType();

        // 2. 고정 MealSchedule 조회
        MealSchedule schedule = mealScheduleRepository.findByMealTypeAndDay(mealType, day)
                .orElseThrow(() -> new IllegalArgumentException("MealSchedule 없음: " + day + " " + mealType));

        // 3. 기존 연결된 MealScheduleItem 삭제
        itemRepository.deleteAllByMealSchedule(schedule);

        // 4. 새로운 메뉴 연결
        String[] items = event.getMealContents().split(" ");
        List<MealScheduleItem> newItems = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            String name = items[i];

            Meal meal = mealRepository.findByName(name)
                    .orElseGet(() -> mealRepository.save(Meal.builder().name(name).build()));

            MealScheduleItem item = MealScheduleItem.builder()
                    .meal(meal)
                    .mealSchedule(schedule)
                    .orderNum(i + 1)
                    .build();

            newItems.add(item);
        }
        itemRepository.saveAll(newItems);
    }

    private String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    public List<MealScheduleResponse> getWeeklyMeals() {
        List<String> days = List.of("월", "화", "수", "목", "금");

        return mealScheduleRepository.findAllByDayIn(days).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MealScheduleResponse getDailyMeal(String day, String mealType) {
        MealSchedule schedule = mealScheduleRepository.findByMealTypeAndDay(mealType, day)
                .orElseThrow(() -> new IllegalArgumentException("해당 식단표 없음"));

        return mapToResponse(schedule);
    }

    private MealScheduleResponse mapToResponse(MealSchedule schedule) {
        List<MealScheduleItem> items = itemRepository.findAllByMealScheduleOrderByOrderNum(schedule);
        List<String> menus = items.stream()
                .map(item -> item.getMeal().getName())
                .toList();

        return MealScheduleResponse.builder()
                .mealType(schedule.getMealType())
                .day(schedule.getDay())
                .menus(menus)
                .build();
    }

    @PostConstruct
    public void initMealSchedules() {
        List<String> days = List.of("월", "화", "수", "목", "금");
        List<String> mealTypes = List.of("조식", "중식", "석식");

        for (String day : days) {
            for (String mealType : mealTypes) {
                boolean exists = mealScheduleRepository.existsByMealTypeAndDay(mealType, day);
                if (!exists) {
                    mealScheduleRepository.save(MealSchedule.builder()
                            .day(day)
                            .mealType(mealType)
                            .build());
                }
            }
        }
    }
}

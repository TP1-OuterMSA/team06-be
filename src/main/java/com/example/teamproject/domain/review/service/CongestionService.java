package com.example.teamproject.domain.review.service;

import com.example.teamproject.domain.review.dto.CongestionResponseDto;
import com.example.teamproject.domain.review.entity.Review;
import com.example.teamproject.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CongestionService {
    private final ReviewRepository reviewRepository;

    public List<CongestionResponseDto> getCongestionByMealTime(String mealTime) {
        // mealTime에 따른 visitTime 슬롯 생성
        List<String> expectedTimeSlots = getTimeSlots(mealTime);
        // mealTime별 리뷰 조회
        List<Review> reviews = reviewRepository.findByMealTime(mealTime);

        // 방문시간(visitTime)별로 리뷰 그룹핑
        Map<String, List<Review>> reviewsByVisitTime = reviews.stream()
                .collect(Collectors.groupingBy(Review::getVisitTime));

        List<CongestionResponseDto> result = new ArrayList<>();
        for (String timeSlot : expectedTimeSlots) {
            List<Review> reviewsForSlot = reviewsByVisitTime.getOrDefault(timeSlot, Collections.emptyList());
            double avgScore = 0.0;
            if (!reviewsForSlot.isEmpty()) {
                avgScore = reviewsForSlot.stream()
                        .mapToInt(r -> convertCongestionLevelToScore(r.getCongestionLevel()))
                        .average()
                        .orElse(0.0);
            }
            // 소수점 첫째자리까지 반올림
            avgScore = Math.round(avgScore * 10) / 10.0;
            result.add(new CongestionResponseDto(timeSlot, avgScore));
        }
        return result;
    }

    // mealTime에 따른 방문시간 슬롯 생성
    private List<String> getTimeSlots(String mealTime) {
        List<String> timeSlots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime start = LocalTime.of(0, 0);
        LocalTime end =LocalTime.of(0, 0);
        switch(mealTime){
            case "조식":
                start = LocalTime.of(8, 0);
                end = LocalTime.of(9, 0);
                break;
            case "중식":
                start = LocalTime.of(11, 30);
                end = LocalTime.of(14, 0);
                break;
            case "석식":
                start = LocalTime.of(17, 0);
                end = LocalTime.of(18, 30);
                break;
        }
        while(start.isBefore(end)) {
            timeSlots.add(start.format(formatter));
            start = start.plusMinutes(15);
        }
        return timeSlots;
    }

    // 혼잡도 문자열을 점수로 변환 (매우 많음: 100, 많음: 80, 보통: 60, 적음: 40, 매우 적음: 20)
    private int convertCongestionLevelToScore(String level) {
        switch(level) {
            case "매우 많음": return 100;
            case "많음": return 80;
            case "보통": return 60;
            case "적음": return 40;
            case "매우 적음": return 20;
            default: return 0;
        }
    }
}

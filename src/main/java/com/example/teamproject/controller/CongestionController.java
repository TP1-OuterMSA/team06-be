package com.example.teamproject.controller;

import com.example.teamproject.domain.review.dto.CongestionResponseDto;
import com.example.teamproject.domain.review.service.CongestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/team6/congestion")
@RequiredArgsConstructor
public class CongestionController {
    private final CongestionService congestionService;

    @GetMapping("/breakfast")
    public ResponseEntity<List<CongestionResponseDto>> getBreakfastCongestion() {
        List<CongestionResponseDto> result = congestionService.getCongestionByMealTime("조식")
                .stream()
                .map(dto -> new CongestionResponseDto(dto.getVisitTime(), dto.getAverageScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/lunch")
    public ResponseEntity<List<CongestionResponseDto>> getLunchCongestion() {
        List<CongestionResponseDto> result = congestionService.getCongestionByMealTime("중식")
                .stream()
                .map(dto -> new CongestionResponseDto(dto.getVisitTime(), dto.getAverageScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dinner")
    public ResponseEntity<List<CongestionResponseDto>> getDinnerCongestion() {
        List<CongestionResponseDto> result = congestionService.getCongestionByMealTime("석식")
                .stream()
                .map(dto -> new CongestionResponseDto(dto.getVisitTime(), dto.getAverageScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}

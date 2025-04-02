package com.example.teamproject.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CongestionResponseDto {
    private String visitTime;
    private double averageScore;
}

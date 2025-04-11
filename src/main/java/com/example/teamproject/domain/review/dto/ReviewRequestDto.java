package com.example.teamproject.domain.review.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRequestDto {
    private String mealTime;
    private String visitTime;
    private String congestionLevel;
}

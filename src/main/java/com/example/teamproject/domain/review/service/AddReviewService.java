package com.example.teamproject.domain.review.service;


import com.example.teamproject.domain.review.dto.ReviewRequestDto;
import com.example.teamproject.domain.review.entity.Review;
import com.example.teamproject.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddReviewService {
    private final ReviewRepository reviewRepository;

    public Review addReview(ReviewRequestDto reviewRequestDto) {
        Review review = new Review();
        review.setMealTime(reviewRequestDto.getMealTime());
        review.setVisitTime(reviewRequestDto.getVisitTime());
        review.setCongestionLevel(reviewRequestDto.getCongestionLevel());
        return reviewRepository.save(review);
    }

    public List<Review> addReviews(List<ReviewRequestDto> reviewRequestDtos) {
        List<Review> reviews = reviewRequestDtos.stream().map(dto -> {
            Review review = new Review();
            review.setMealTime(dto.getMealTime());
            review.setVisitTime(dto.getVisitTime());
            review.setCongestionLevel(dto.getCongestionLevel());
            return review;
        }).collect(Collectors.toList());
        return reviewRepository.saveAll(reviews);
    }
}

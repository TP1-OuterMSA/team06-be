package com.example.teamproject.controller;
import com.example.teamproject.domain.review.dto.ReviewRequestDto;
import com.example.teamproject.domain.review.entity.Review;
import com.example.teamproject.domain.review.service.AddReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/team6/reviews")
@RequiredArgsConstructor
public class AddReviewController {
    private final AddReviewService addreviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        Review savedReview = addreviewService.addReview(reviewRequestDto);
        return ResponseEntity.ok(savedReview);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Review>> addReviews(@RequestBody List<ReviewRequestDto> reviewRequestDtos) {
        List<Review> savedReviews = addreviewService.addReviews(reviewRequestDtos);
        return ResponseEntity.ok(savedReviews);
    }
}

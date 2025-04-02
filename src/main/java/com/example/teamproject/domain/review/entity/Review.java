package com.example.teamproject.domain.review.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mealTime;

    @Column(nullable = false)
    private String visitTime;

    @Column(nullable = false)
    private String congestionLevel;
}

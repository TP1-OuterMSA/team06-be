package com.example.teamproject.domain.userAllergy.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateUserAllergyDto {
    private Long userId;
    private List<Long> allergies;
}

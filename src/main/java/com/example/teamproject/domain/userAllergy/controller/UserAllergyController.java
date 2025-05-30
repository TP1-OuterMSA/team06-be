package com.example.teamproject.domain.userAllergy.controller;

import com.example.teamproject.domain.userAllergy.dto.request.UpdateUserAllergyDto;
import com.example.teamproject.domain.userAllergy.dto.response.UserAllergyDto;
import com.example.teamproject.domain.userAllergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team6/user/userAllergy")
@RequiredArgsConstructor
public class UserAllergyController {
    private final UserAllergyService userAllergyService;

    @PutMapping("/update")
    public void updateUserAllergy(
            @RequestHeader("userId") Long userId,
            @RequestBody UpdateUserAllergyDto updateUserAllergyDto) {
        userAllergyService.replaceUserAllergies(userId, updateUserAllergyDto.getAllergies());
    }

    @GetMapping("/me")
    public UserAllergyDto getMyAllergies(
            @RequestHeader("userId") Long userId ) {
        return userAllergyService.getMyAllergies(userId);
    }

}

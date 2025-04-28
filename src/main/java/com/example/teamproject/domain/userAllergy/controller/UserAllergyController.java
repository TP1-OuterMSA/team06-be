package com.example.teamproject.domain.userAllergy.controller;

import com.example.teamproject.domain.userAllergy.dto.request.UpdateUserAllergyDto;
import com.example.teamproject.domain.userAllergy.dto.response.UserAllergyDto;
import com.example.teamproject.domain.userAllergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team6/userAllergy")
@RequiredArgsConstructor
public class UserAllergyController {
    private final UserAllergyService userAllergyService;

    @PutMapping("/update")
    public void updateUserAllergy(@RequestBody UpdateUserAllergyDto updateUserAllergyDto) {
        userAllergyService.replaceUserAllergies(updateUserAllergyDto.getUserId(), updateUserAllergyDto.getAllergies());
    }

    @GetMapping("/{userId}")
    public UserAllergyDto getMyAllergies(@PathVariable Long userId) {
        return userAllergyService.getMyAllergies(userId);
    }

}

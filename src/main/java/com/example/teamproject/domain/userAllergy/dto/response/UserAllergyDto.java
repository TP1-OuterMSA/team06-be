package com.example.teamproject.domain.userAllergy.dto.response;

import com.example.teamproject.domain.allergy.dto.response.AllergyDto;
import com.example.teamproject.domain.allergy.entity.Allergy;
import lombok.Getter;

import java.util.List;

@Getter
public class UserAllergyDto {
    private List<AllergyDto> allergies;

    public UserAllergyDto(List<Allergy> allergies) {
        this.allergies = allergies.stream()
                .map(AllergyDto::new)
                .toList();
    }

}

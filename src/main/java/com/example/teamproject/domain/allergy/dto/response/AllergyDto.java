package com.example.teamproject.domain.allergy.dto.response;

import com.example.teamproject.domain.allergy.entity.Allergy;
import lombok.Getter;

@Getter
public class AllergyDto {
    private Long id;
    private String name;

    public AllergyDto(Allergy allergy){
        this.id = allergy.getId();
        this.name = allergy.getName();
    }
}

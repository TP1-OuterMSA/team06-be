package com.example.teamproject.domain.allergy.service;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.repository.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;

    public Allergy addAllergy(String name) {
        return allergyRepository.save(Allergy.of(name));
    }

    public Allergy getAllergyById(Long allergyId) {
        return allergyRepository.findById(allergyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알레르기입니다."));
    }
}

package com.example.teamproject.domain.user.service;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.service.AllergyService;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.entity.UserAllergy;
import com.example.teamproject.domain.user.repository.UserAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAllergyService {

    private final AllergyService allergyService;
    private final UserAllergyRepository userAllergyRepository;

    public void saveUserAllergy(User user, Long allergyId) {
        Allergy allergy = allergyService.getAllergyById(allergyId);
        userAllergyRepository.save(UserAllergy.of(user, allergy));
    }
}

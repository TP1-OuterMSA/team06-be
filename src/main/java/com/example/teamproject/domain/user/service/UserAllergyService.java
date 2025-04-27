package com.example.teamproject.domain.user.service;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.service.AllergyService;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.entity.UserAllergy;
import com.example.teamproject.domain.user.repository.UserAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAllergyService {

    private final AllergyService allergyService;
    private final UserAllergyRepository userAllergyRepository;

    public void saveUserAllergy(User user, Long allergyId) {
        Allergy allergy = allergyService.getAllergyById(allergyId);
        userAllergyRepository.save(UserAllergy.of(user, allergy));
    }

    public List<String> getAllergyNamesByUserId(Long userId) {
        return userAllergyRepository.findByUserId(userId).stream()
                .map(ua -> ua.getAllergy().getName())
                .toList();
    }

    @Transactional
    public void replaceUserAllergies(User user, List<Long> allergyIds) {
        if (allergyIds == null) return;                 // 변경 없음

        userAllergyRepository.deleteByUserId(user.getId());

        if (!allergyIds.isEmpty()) {
            for (Long id : allergyIds) saveUserAllergy(user, id);
        }
    }
}

package com.example.teamproject.domain.userAllergy.service;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.service.AllergyService;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.domain.user.service.UserService;
import com.example.teamproject.domain.userAllergy.dto.request.UpdateUserAllergyDto;
import com.example.teamproject.domain.userAllergy.dto.response.UserAllergyDto;
import com.example.teamproject.domain.userAllergy.entity.UserAllergy;
import com.example.teamproject.domain.userAllergy.repository.UserAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAllergyService {

    private final AllergyService allergyService;
    private final UserAllergyRepository userAllergyRepository;
    private final UserRepository userRepository;

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
    public void replaceUserAllergies(Long userId, List<Long> allergyIds) {
        if (allergyIds == null) return;                 // 변경 없음
        User user = findUserById(userId);
        userAllergyRepository.deleteByUserId(user.getId());

        if (!allergyIds.isEmpty()) {
            for (Long id : allergyIds) saveUserAllergy(user, id);
        }
    }

    private User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
    }

    public UserAllergyDto getMyAllergies(Long userId) {
        List<Allergy> allergies = userAllergyRepository.findByUserId(userId).stream()
                .map(UserAllergy::getAllergy)
                .toList();
        return new UserAllergyDto(allergies);

    }
}

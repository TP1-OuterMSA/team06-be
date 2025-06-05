package com.example.teamproject.domain.allergy.service;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.repository.AllergyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Allergy> getAllAllergies() {
        return allergyRepository.findAll();
    }

//    @PostConstruct
//    public void initAllergyData() {
//        if (allergyRepository.count() > 0) {
//            return; // 이미 존재하면 초기화 건너뜀
//        }
//
//        List<String> allergyNames = List.of(
//                "알류", "우유", "메밀", "땅콩", "대두", "밀", "잣", "호두", "게", "새우",
//                "오징어", "고등어", "조개류", "복숭아", "토마토",
//                "닭고기", "돼지고기", "쇠고기", "아황산류"
//        );
//
//        List<Allergy> allergies = allergyNames.stream()
//                .map(Allergy::of)
//                .toList();
//
//        allergyRepository.saveAll(allergies);
//    }
}

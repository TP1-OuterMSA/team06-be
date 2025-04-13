package com.example.teamproject.domain.allergy.controller;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.service.AllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team6/allergy")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping("/{name}")
    public ResponseEntity<Allergy> addAllergy(@PathVariable String name){
        return ResponseEntity.ok(allergyService.addAllergy(name));
    }

}

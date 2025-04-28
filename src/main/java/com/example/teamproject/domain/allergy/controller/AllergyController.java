package com.example.teamproject.domain.allergy.controller;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.service.AllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team6/allergy")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping("/{name}")
    public ResponseEntity<Allergy> addAllergy(@PathVariable String name){
        return ResponseEntity.ok(allergyService.addAllergy(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Allergy>> getAllAllergies(){
        return ResponseEntity.ok(allergyService.getAllAllergies());
    }

}

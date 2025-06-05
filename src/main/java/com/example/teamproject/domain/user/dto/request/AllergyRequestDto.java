package com.example.teamproject.domain.user.dto.request;



import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AllergyRequestDto {
    @NotBlank(message = "알레르기 이름을 입력해주세요.")
    private String allergyName;
}

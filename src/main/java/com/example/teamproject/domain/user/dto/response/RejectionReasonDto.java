package com.example.teamproject.domain.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RejectionReasonDto {
    @NotBlank(message = "거절 사유를 입력해주세요.")
    private String reason;
}
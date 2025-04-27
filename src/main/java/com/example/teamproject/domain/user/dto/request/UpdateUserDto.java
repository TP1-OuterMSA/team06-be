package com.example.teamproject.domain.user.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateUserDto {
    private String username;      // null 이면 변경 X
    private String email;
    private String password;
    private List<Long> allergies; // null ⇒ 변경 X, 빈 배열 ⇒ 모두 제거
}

package com.example.teamproject.domain.user.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SignupDto {
    private String username;
    private String email;
    private String password;
    private List<Long> allergies;
}

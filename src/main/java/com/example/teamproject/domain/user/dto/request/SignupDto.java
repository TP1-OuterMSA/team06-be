package com.example.teamproject.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class SignupDto {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private List<Long> allergies;
}

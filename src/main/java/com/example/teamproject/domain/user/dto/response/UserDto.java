package com.example.teamproject.domain.user.dto.response;

import com.example.teamproject.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private List<String> allergies;
    private String profileImageUrl;

    public static UserDto from(User user, List<String> allergies) {
        String imgUrl = "/api/team6/user/" + user.getId() + "/profile-image";
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .allergies(allergies)
                .profileImageUrl(imgUrl)
                .build();
    }
}

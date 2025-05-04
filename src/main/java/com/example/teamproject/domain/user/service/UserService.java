package com.example.teamproject.domain.user.service;

import com.example.teamproject.domain.user.dto.request.LoginDto;
import com.example.teamproject.domain.user.dto.request.SignupDto;
import com.example.teamproject.domain.user.dto.request.UpdateUserDto;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.domain.userAllergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAllergyService userAllergyService;

    public UserDto signup(SignupDto signupDto) {
        if(userRepository.existsByUsername(signupDto.getUsername()))
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");

        User user = User.from(signupDto);
        userRepository.save(user);

        List<Long> allergies = signupDto.getAllergies();
        if (allergies != null && !allergies.isEmpty()) {
            for (Long allergyId : allergies) userAllergyService.saveUserAllergy(user, allergyId);
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .build();
    }

    public UserDto login(LoginDto loginDto) {
        User user = getUserByUsername(loginDto.getUsername());
        if (!user.getPassword().equals(loginDto.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .build();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public UserDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<String> allergyNames = userAllergyService.getAllergyNamesByUserId(userId);
        return UserDto.from(user, allergyNames);
    }

    @Transactional
    public UserDto updateUser(Long userId, UpdateUserDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getNickname() != null && !dto.getNickname().equals(user.getUsername())) {
//            if (userRepository.existsByUsername(dto.getNickname()))
//                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail()    != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());

        userAllergyService.replaceUserAllergies(user.getId(), dto.getAllergies());

        List<String> allergyNames = userAllergyService.getAllergyNamesByUserId(userId);
        return UserDto.from(user, allergyNames);
    }

    @Transactional
    public void saveProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        try {
            user.setProfileImage(file.getBytes());
            user.setProfileImageType(file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("이미지 변환 실패", e);
        }
    }

    @Transactional(readOnly = true)
    public Pair<byte[], String> loadProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        if (user.getProfileImage() == null)
            throw new NoSuchElementException("저장된 프로필 이미지가 없습니다.");

        return Pair.of(user.getProfileImage(), user.getProfileImageType());
    }

}
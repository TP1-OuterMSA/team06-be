package com.example.teamproject.domain.user.service;

import com.example.teamproject.config.security.JwtTokenProvider;
import com.example.teamproject.domain.user.dto.request.LoginDto;
import com.example.teamproject.domain.user.dto.request.SignupDto;
import com.example.teamproject.domain.user.dto.request.UpdateUserDto;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.domain.userAllergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAllergyService userAllergyService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    public UserDto signup(SignupDto signupDto) {

        if(userRepository.existsByUsername(signupDto.getUsername()))
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");

        User user = User.from(signupDto);
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));

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

    /**
     * 로그인 → AuthenticationManager 인증 → JWT 발급 → UserDto 반환
     */
    public UserDto login(LoginDto loginDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(auth);

        User user = getUserEntityByUsername(loginDto.getUsername());
        List<String> allergyNames = userAllergyService.getAllergyNamesByUserId(user.getId());
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .allergies(allergyNames)
                .jwtToken(token)
                .build();
    }

    /**
     * 현재 로그인한 사용자(username)로 Profile 조회
     */
    @Transactional(readOnly = true)
    public UserDto getByUsername(String username) {
        User user = getUserEntityByUsername(username);
        List<String> allergyNames = userAllergyService.getAllergyNamesByUserId(user.getId());
        return UserDto.from(user, allergyNames);
    }

    /**
     * 현재 로그인한 사용자(username) 정보 수정
     */
    @Transactional
    public UserDto updateByUsername(String username, UpdateUserDto dto) {
        User user = getUserEntityByUsername(username);

        if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userAllergyService.replaceUserAllergies(user.getId(), dto.getAllergies());

        List<String> allergyNames = userAllergyService.getAllergyNamesByUserId(user.getId());
        return UserDto.from(user, allergyNames);
    }

    /**
     * 현재 로그인한 사용자(username) 프로필 이미지 저장
     */
    @Transactional
    public void saveProfileImageByUsername(String username, MultipartFile file) {
        User user = getUserEntityByUsername(username);
        try {
            user.setProfileImage(file.getBytes());
            user.setProfileImageType(file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    /**
     * 현재 로그인한 사용자(username) 프로필 이미지 로드
     */
    @Transactional(readOnly = true)
    public Pair<byte[], String> loadProfileImageByUsername(String username) {
        User user = getUserEntityByUsername(username);
        if (user.getProfileImage() == null) {
            throw new IllegalStateException("저장된 프로필 이미지가 없습니다.");
        }
        return Pair.of(user.getProfileImage(), user.getProfileImageType());
    }

    // --- 내부 헬퍼 메서드 ---
    private User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}

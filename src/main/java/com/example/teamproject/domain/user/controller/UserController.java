package com.example.teamproject.domain.user.controller;

import com.example.teamproject.domain.user.dto.request.LoginDto;
import com.example.teamproject.domain.user.dto.request.SignupDto;
import com.example.teamproject.domain.user.dto.request.UpdateUserDto;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/team6/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 회원가입 → JWT 발급 */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto) {
        UserDto signed = userService.signup(signupDto);
        return ResponseEntity.ok(signed);
    }

    /** 로그인 → JWT 발급 */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto) {
        UserDto logged = userService.login(loginDto);
        return ResponseEntity.ok(logged);
    }

    /** 내 정보 조회 (인증 필요) */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getMyPage(
            @AuthenticationPrincipal UserDetails principal
    ) {
        // principal.getUsername() 으로 현재 사용자 조회
        UserDto me = userService.getByUsername(principal.getUsername());
        return ResponseEntity.ok(me);
    }

    /** 내 정보 수정 (인증 필요) */
    @PatchMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateUser(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody UpdateUserDto dto
    ) {
        UserDto updated = userService.updateByUsername(principal.getUsername(), dto);
        return ResponseEntity.ok(updated);
    }

    /** 나의 프로필 이미지 업로드 (인증 필요) */
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal UserDetails principal,
            @RequestPart("file") MultipartFile file
    ) {
        userService.saveProfileImageByUsername(principal.getUsername(), file);
        return ResponseEntity.ok("프로필 이미지 저장 완료");
    }

    /** 나의 프로필 이미지 다운로드 (인증 필요) */
    @GetMapping("/profile-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ByteArrayResource> downloadProfileImage(
            @AuthenticationPrincipal UserDetails principal
    ) {
        Pair<byte[], String> data = userService.loadProfileImageByUsername(principal.getUsername());
        ByteArrayResource resource = new ByteArrayResource(data.getFirst());
        return ResponseEntity.ok()
                .contentLength(data.getFirst().length)
                .contentType(MediaType.parseMediaType(data.getSecond()))
                .body(resource);
    }
}

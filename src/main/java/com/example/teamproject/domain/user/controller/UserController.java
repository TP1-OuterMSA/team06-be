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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/team6/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto){
        return ResponseEntity.ok(userService.signup(signupDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userService.login(loginDto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyPage(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PatchMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserDto dto) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable("id") Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        userService.saveProfileImage(userId, file);
        return ResponseEntity.ok("프로필 이미지 저장 완료");
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<ByteArrayResource> downloadProfileImage(
            @PathVariable("id") Long userId
    ) {
        Pair<byte[], String> data = userService.loadProfileImage(userId);
        byte[] imageBytes = data.getFirst();
        String contentType = data.getSecond();

        ByteArrayResource resource = new ByteArrayResource(imageBytes);
        return ResponseEntity.ok()
                .contentLength(imageBytes.length)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}

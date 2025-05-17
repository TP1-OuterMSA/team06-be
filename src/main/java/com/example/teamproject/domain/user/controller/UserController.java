package com.example.teamproject.domain.user.controller;

import com.example.teamproject.domain.user.dto.request.LoginDto;
import com.example.teamproject.domain.user.dto.request.SignupDto;
import com.example.teamproject.domain.user.dto.request.UpdateUserDto;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.PromotionRequest;
import com.example.teamproject.domain.user.service.PromotionService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team6/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PromotionService promotionService;

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


    /** 1) 회원 → 관리자 승격 요청 */
    @PostMapping("/promotion/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> requestPromotion(
            @AuthenticationPrincipal UserDetails principal
    ) {
        promotionService.requestPromotion(principal.getUsername());
        return ResponseEntity.ok("승격 요청이 관리자에게 전달되었습니다.");
    }

    /** 2) ADMIN → 특정 요청 승인 */
    @PostMapping("/promotion/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approvePromotion(@PathVariable Long id) {
        promotionService.approve(id);
        return ResponseEntity.ok("승격 요청이 승인되었습니다.");
    }

    /** 3) ADMIN → 특정 요청 거절 */
    @PostMapping("/promotion/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejectPromotion(@PathVariable Long id) {
        promotionService.reject(id);
        return ResponseEntity.ok("승격 요청이 거절되었습니다.");
    }

    /** 4) ADMIN → 대기 중인 요청 조회 (선택) */
    @GetMapping("/promotion/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PromotionRequest>> listPending() {
        return ResponseEntity.ok(promotionService.listPending());
    }

    /** 내 승격 요청 상태 조회 (USER 권한 이상) */
    @GetMapping("/promotion/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getPromotionStatus(
            @AuthenticationPrincipal UserDetails principal
    ) {
        String status = promotionService.getMyPromotionStatus(principal.getUsername());
        return ResponseEntity.ok(Map.of("status", status));
    }
}

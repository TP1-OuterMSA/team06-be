package com.example.teamproject.domain.user.controller;

import com.example.teamproject.domain.user.dto.request.AllergyRequestDto;
import com.example.teamproject.domain.user.dto.request.UpdateUserDto;
import com.example.teamproject.domain.user.dto.response.RejectionReasonDto;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.AllergyRequest;
import com.example.teamproject.domain.user.entity.PromotionRequest;
import com.example.teamproject.domain.user.service.AllergyRequestService;
import com.example.teamproject.domain.user.service.PromotionService;
import com.example.teamproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/team6/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PromotionService promotionService;
    private final AllergyRequestService allergyRequestService;

//    /** 회원가입 → (delegated to auth server) */
//    @PostMapping("/signup")
//    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto) {
//        UserDto signed = userService.signup(signupDto);
//        return ResponseEntity.ok(signed);
//    }

    /** 내 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyPage(
            @RequestHeader("userId") Long userId
    ) {
        UserDto me = userService.getByUserId(userId);
        return ResponseEntity.ok(me);
    }

    /** 내 정보 수정 */
    @PatchMapping("/update")
    public ResponseEntity<UserDto> updateUser(
            @RequestHeader("userId") Long userId,
            @RequestBody UpdateUserDto dto
    ) {
        UserDto updated = userService.updateByUserId(userId, dto);
        return ResponseEntity.ok(updated);
    }

    /** 프로필 이미지 업로드 */
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfileImage(
            @RequestHeader("userId") Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        userService.saveProfileImageByUserId(userId, file);
        return ResponseEntity.ok("프로필 이미지 저장 완료");
    }

    /** 프로필 이미지 다운로드 */
    @GetMapping("/profile-image")
    public ResponseEntity<ByteArrayResource> downloadProfileImage(
            @RequestHeader("userId") Long userId
    ) {
        var data = userService.loadProfileImageByUserId(userId);
        ByteArrayResource resource = new ByteArrayResource(data.getFirst());
        return ResponseEntity.ok()
                .contentLength(data.getFirst().length)
                .contentType(MediaType.parseMediaType(data.getSecond()))
                .body(resource);
    }

    /** 사용자 → 관리자 승격 요청 */
    @PostMapping("/promotion/request")
    public ResponseEntity<String> requestPromotion(
            @RequestHeader("userId") Long userId
    ) {
        promotionService.requestPromotion(userId);
        return ResponseEntity.ok("승격 요청이 관리자에게 전달되었습니다.");
    }

    /** 내 승격 요청 상태 조회 */
    @GetMapping("/promotion/status")
    public ResponseEntity<Map<String, String>> getPromotionStatus(
            @RequestHeader("userId") Long userId
    ) {
        String status = promotionService.getMyPromotionStatus(userId);
        return ResponseEntity.ok(Map.of("status", status));
    }

    /** 일반 유저 → 알레르기 추가 요청 */
    @PostMapping("/allergy-request")
    public ResponseEntity<String> requestAllergy(
            @RequestHeader("userId") Long userId,
            @RequestBody @Valid AllergyRequestDto dto
    ) {
        allergyRequestService.requestAllergyAddition(userId, dto);
        return ResponseEntity.ok("알레르기 추가 요청이 관리자에게 전달되었습니다.");
    }

}

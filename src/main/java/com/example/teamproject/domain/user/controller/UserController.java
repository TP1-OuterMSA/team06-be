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
            @RequestHeader("username") String username
    ) {
        UserDto me = userService.getByUsername(username);
        return ResponseEntity.ok(me);
    }

    /** 내 정보 수정 */
    @PatchMapping("/update")
    public ResponseEntity<UserDto> updateUser(
            @RequestHeader("username") String username,
            @RequestBody UpdateUserDto dto
    ) {
        UserDto updated = userService.updateByUsername(username, dto);
        return ResponseEntity.ok(updated);
    }

    /** 프로필 이미지 업로드 */
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfileImage(
            @RequestHeader("username") String username,
            @RequestPart("file") MultipartFile file
    ) {
        userService.saveProfileImageByUsername(username, file);
        return ResponseEntity.ok("프로필 이미지 저장 완료");
    }

    /** 프로필 이미지 다운로드 */
    @GetMapping("/profile-image")
    public ResponseEntity<ByteArrayResource> downloadProfileImage(
            @RequestHeader("username") String username
    ) {
        var data = userService.loadProfileImageByUsername(username);
        ByteArrayResource resource = new ByteArrayResource(data.getFirst());
        return ResponseEntity.ok()
                .contentLength(data.getFirst().length)
                .contentType(MediaType.parseMediaType(data.getSecond()))
                .body(resource);
    }

    /** 사용자 → 관리자 승격 요청 */
    @PostMapping("/promotion/request")
    public ResponseEntity<String> requestPromotion(
            @RequestHeader("username") String username
    ) {
        promotionService.requestPromotion(username);
        return ResponseEntity.ok("승격 요청이 관리자에게 전달되었습니다.");
    }

    /** ADMIN → 특정 요청 승인 */
    @PostMapping("/promotion/approve/{id}")
    public ResponseEntity<String> approvePromotion(
            @RequestHeader("username") String username,
            @PathVariable Long id
    ) {
        promotionService.approve(id);
        return ResponseEntity.ok("승격 요청이 승인되었습니다.");
    }

    /** ADMIN → 특정 요청 거절 (사유 포함) */
    @PostMapping("/promotion/reject/{id}")
    public ResponseEntity<String> rejectPromotion(
            @RequestHeader("username") String username,
            @PathVariable Long id,
            @RequestBody @Valid RejectionReasonDto dto
    ) {
        promotionService.reject(id, dto.getReason());
        return ResponseEntity.ok("승격 요청이 거절되었습니다.");
    }

    /** ADMIN → 대기 중인 요청 조회 */
    @GetMapping("/promotion/pending")
    public ResponseEntity<List<PromotionRequest>> listPending(
            @RequestHeader("username") String username
    ) {
        return ResponseEntity.ok(promotionService.listPending());
    }

    /** 내 승격 요청 상태 조회 */
    @GetMapping("/promotion/status")
    public ResponseEntity<Map<String, String>> getPromotionStatus(
            @RequestHeader("username") String username
    ) {
        String status = promotionService.getMyPromotionStatus(username);
        return ResponseEntity.ok(Map.of("status", status));
    }

    /** 일반 유저 → 알레르기 추가 요청 */
    @PostMapping("/allergy-request")
    public ResponseEntity<String> requestAllergy(
            @RequestHeader("username") String username,
            @RequestBody @Valid AllergyRequestDto dto
    ) {
        allergyRequestService.requestAllergyAddition(username, dto);
        return ResponseEntity.ok("알레르기 추가 요청이 관리자에게 전달되었습니다.");
    }

    /** ADMIN → 알레르기 요청 승인 */
    @PostMapping("/allergy-request/approve/{id}")
    public ResponseEntity<String> approveAllergy(
            @RequestHeader("username") String username,
            @PathVariable Long id
    ) {
        allergyRequestService.approveAllergy(id);
        return ResponseEntity.ok("알레르기 요청이 승인되었습니다.");
    }

    /** ADMIN → 알레르기 요청 거절 */
    @PostMapping("/allergy-request/reject/{id}")
    public ResponseEntity<String> rejectAllergy(
            @RequestHeader("username") String username,
            @PathVariable Long id,
            @RequestBody @Valid RejectionReasonDto dto    // 사유를 본문으로 받습니다
    ) {
        allergyRequestService.rejectAllergy(id, dto.getReason());
        return ResponseEntity.ok("알레르기 요청이 거절되었습니다.");
    }

    /** ADMIN → 대기 중인 알레르기 요청 조회 */
    @GetMapping("/allergy-request/pending")
    public ResponseEntity<List<AllergyRequest>> listPendingAllergies(
            @RequestHeader("username") String username
    ) {
        // 여기에리포지토리 메서드 추가 후 사용 가능
        return ResponseEntity.ok(allergyRequestService.listPendingRequests());
    }

}

package com.example.teamproject.domain.user.controller;

import com.example.teamproject.domain.user.dto.response.RejectionReasonDto;
import com.example.teamproject.domain.user.entity.AllergyRequest;
import com.example.teamproject.domain.user.entity.PromotionRequest;
import com.example.teamproject.domain.user.service.AllergyRequestService;
import com.example.teamproject.domain.user.service.PromotionService;
import com.example.teamproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team6/admin")
public class AdminController {
    private final UserService userService;
    private final PromotionService promotionService;
    private final AllergyRequestService allergyRequestService;

    /** ADMIN → 특정 요청 승인 */
    @PostMapping("/promotion/approve/{id}")
    public ResponseEntity<String> approvePromotion(
            @RequestHeader("userId") Long userId,
            @PathVariable Long id
    ) {
        promotionService.approve(id);
        return ResponseEntity.ok("승격 요청이 승인되었습니다.");
    }

    /** ADMIN → 특정 요청 거절 (사유 포함) */
    @PostMapping("/promotion/reject/{id}")
    public ResponseEntity<String> rejectPromotion(
            @RequestHeader("userId") Long userId,
            @PathVariable Long id,
            @RequestBody @Valid RejectionReasonDto dto
    ) {
        promotionService.reject(id, dto.getReason());
        return ResponseEntity.ok("승격 요청이 거절되었습니다.");
    }

    /** ADMIN → 대기 중인 요청 조회 */
    @GetMapping("/promotion/pending")
    public ResponseEntity<List<PromotionRequest>> listPending(
            @RequestHeader("userId") Long userId
    ) {
        return ResponseEntity.ok(promotionService.listPending());
    }


    /** ADMIN → 알레르기 요청 승인 */
    @PostMapping("/allergy-request/approve/{id}")
    public ResponseEntity<String> approveAllergy(
            @RequestHeader("userId") Long userId,
            @PathVariable Long id
    ) {
        allergyRequestService.approveAllergy(id);
        return ResponseEntity.ok("알레르기 요청이 승인되었습니다.");
    }

    /** ADMIN → 알레르기 요청 거절 */
    @PostMapping("/allergy-request/reject/{id}")
    public ResponseEntity<String> rejectAllergy(
            @RequestHeader("userId") Long userId,
            @PathVariable Long id,
            @RequestBody @Valid RejectionReasonDto dto    // 사유를 본문으로 받습니다
    ) {
        allergyRequestService.rejectAllergy(id, dto.getReason());
        return ResponseEntity.ok("알레르기 요청이 거절되었습니다.");
    }

    /** ADMIN → 대기 중인 알레르기 요청 조회 */
    @GetMapping("/allergy-request/pending")
    public ResponseEntity<List<AllergyRequest>> listPendingAllergies(
            @RequestHeader("userId") Long userId
    ) {
        // 여기에리포지토리 메서드 추가 후 사용 가능
        return ResponseEntity.ok(allergyRequestService.listPendingRequests());
    }



}

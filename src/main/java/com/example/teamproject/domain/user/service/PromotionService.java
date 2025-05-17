package com.example.teamproject.domain.user.service;


import com.example.teamproject.domain.user.entity.PromotionRequest;
import com.example.teamproject.domain.user.entity.PromotionRequest.Status;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.PromotionRequestRepository;
import com.example.teamproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRequestRepository promoRepo;
    private final UserRepository userRepo;
    private final JavaMailSender mailSender;

    /** 1) 사용자 → 관리자 승격 요청 */
    @Transactional
    public PromotionRequest requestPromotion(String username) {
        // 1) 사용자 조회
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2) 이미 PENDING 요청이 있는지 검사 (람다 대신 for-loop)
        List<PromotionRequest> pendingList = promoRepo.findByStatus(Status.PENDING);
        for (PromotionRequest existing : pendingList) {
            if (existing.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("이미 승인 대기 중인 요청이 있습니다.");
            }
        }

        // 3) 새 요청 저장
        PromotionRequest req = PromotionRequest.builder()
                .user(user)
                .status(Status.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        promoRepo.save(req);

        // 4) ADMIN들에게 메일 발송
        sendRequestEmailsToAdmins(req);

        return req;
    }

    private void sendRequestEmailsToAdmins(PromotionRequest req) {
        // 1) 모든 사용자 조회
        List<User> allUsers = userRepo.findAll();
        // 2) ADMIN만 필터링 (람다 대신 for-loop)
        List<User> admins = new ArrayList<>();
        for (User u : allUsers) {
            if ("ADMIN".equals(u.getRole())) {
                admins.add(u);
            }
        }

        // 3) 각 ADMIN에게 메일 전송
        for (User admin : admins) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(admin.getEmail());
            msg.setSubject("[관리자 승격 요청] " + req.getUser().getUsername());
            msg.setText(
                    "사용자 ID: " + req.getUser().getUsername() + "\n" +
                            "닉네임: " + req.getUser().getNickname() + "\n\n" +
                            "승격 승인: http://localhost:8080/api/team6/user/promotion/approve/" + req.getId() + "\n" +
                            "승격 거절: http://localhost:8080/api/team6/user/promotion/reject/"  + req.getId()
            );
            mailSender.send(msg);
        }
    }

    /** 2) ADMIN → 승격 승인 */
    @Transactional
    public PromotionRequest approve(Long requestId) {
        PromotionRequest req = promoRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));
        if (req.getStatus() != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        req.setStatus(Status.APPROVED);
        req.setHandledAt(LocalDateTime.now());
        req.getUser().setRole("ADMIN");
        return req;
    }

    /** 3) ADMIN → 승격 거절 */
    @Transactional
    public PromotionRequest reject(Long requestId) {
        PromotionRequest req = promoRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));
        if (req.getStatus() != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        req.setStatus(Status.REJECTED);
        req.setHandledAt(LocalDateTime.now());
        return req;
    }

    /** 4) ADMIN → 대기 중인 요청 조회 */
    @Transactional(readOnly = true)
    public List<PromotionRequest> listPending() {
        // 람다 없이 바로 리포지토리 호출만으로 반환합니다.
        return promoRepo.findByStatus(Status.PENDING);
    }
}
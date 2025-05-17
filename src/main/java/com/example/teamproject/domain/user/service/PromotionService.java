package com.example.teamproject.domain.user.service;


import com.example.teamproject.domain.user.entity.PromotionRequest;
import com.example.teamproject.domain.user.entity.PromotionRequest.Status;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.PromotionRequestRepository;
import com.example.teamproject.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        // 2) ADMIN만 필터링
        List<User> admins = new ArrayList<>();
        for (User u : allUsers) {
            if ("ADMIN".equals(u.getRole())) {
                admins.add(u);
            }
        }

        // 3) 각 ADMIN에게 HTML 메일 전송
        for (User admin : admins) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

                helper.setTo(admin.getEmail());
                helper.setSubject("[관리자 승격 요청] " + req.getUser().getUsername() + "님이 관리자 승격을 요청했습니다.");

                String loginUrl = "http://localhost:5173/team6/login";

                String html = ""
                        + "<p>안녕하세요, 관리자님.</p>"
                        + "<p>사용자 <strong>" + req.getUser().getUsername() + "</strong>님이 관리자 승격을 요청했습니다.</p>"
                        + "<p>아래 버튼을 눌러 로그인 후, 관리자 대시보드에서 요청을 확인해주세요.</p>"
                        + "<p style=\"text-align:center; margin:30px 0;\">"
                        + "  <a href=\"" + loginUrl + "\" "
                        + "     style=\"display:inline-block; padding:12px 24px; "
                        + "            background-color:#4F46E5; color:#fff; text-decoration:none; "
                        + "            border-radius:4px; font-weight:bold;\">"
                        + "    승격 요청 처리하러 가기"
                        + "  </a>"
                        + "</p>"
                        + "<hr/>"
                        + "<p style=\"font-size:0.85em; color:#666;\">"
                        + "이 메일은 발신 전용입니다. 궁금하신 점은 개발팀에 문의해주세요."
                        + "</p>";

                helper.setText(html, true);
                mailSender.send(message);
            } catch (MessagingException e) {
                // 로깅
                System.err.println("메일 전송 실패 to " + admin.getEmail());
                e.printStackTrace();
            }
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

    @Transactional(readOnly = true)
    public String getMyPromotionStatus(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 사용자에 대한 모든 요청을 요청 시간 내림차순으로 가져오기
        List<PromotionRequest> list = promoRepo.findByUserOrderByRequestedAtDesc(user);
        if (list.isEmpty()) {
            return "NONE";
        }

        PromotionRequest latest = list.get(0);
        return latest.getStatus().name();  // "PENDING", "APPROVED", "REJECTED"
    }
}
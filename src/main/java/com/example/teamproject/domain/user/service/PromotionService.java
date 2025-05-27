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
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<PromotionRequest> pendingList = promoRepo.findByStatus(Status.PENDING);
        for (PromotionRequest existing : pendingList) {
            if (existing.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("이미 승인 대기 중인 요청이 있습니다.");
            }
        }

        PromotionRequest req = PromotionRequest.builder()
                .user(user)
                .status(Status.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        promoRepo.save(req);

        sendRequestEmailsToAdmins(req);

        return req;
    }

    private void sendRequestEmailsToAdmins(PromotionRequest req) {
        List<User> allUsers = userRepo.findAll();
        List<User> admins = new ArrayList<>();
        for (User u : allUsers) {
            if ("ADMIN".equals(u.getRole())) {
                admins.add(u);
            }
        }

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
                System.err.println("메일 전송 실패 to " + admin.getEmail());
                e.printStackTrace();
            }
        }
    }

    /** 4) ADMIN → 대기 중인 요청 조회 */
    @Transactional(readOnly = true)
    public List<PromotionRequest> listPending() {
        return promoRepo.findByStatus(Status.PENDING);
    }

    @Transactional(readOnly = true)
    public String getMyPromotionStatus(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<PromotionRequest> list = promoRepo.findByUserOrderByRequestedAtDesc(user);
        if (list.isEmpty()) {
            return "NONE";
        }

        PromotionRequest latest = list.get(0);
        return latest.getStatus().name();  // "PENDING", "APPROVED", "REJECTED"
    }

    /** ADMIN → 승격 승인 */
    @Transactional
    public PromotionRequest approve(Long requestId) {
        PromotionRequest req = findPending(requestId);
        req.setStatus(Status.APPROVED);
        req.setHandledAt(LocalDateTime.now());
        req.getUser().setRole("ADMIN");
        sendUserNotification(req, true, null);
        return req;
    }

    /** ADMIN → 승격 거절 (사유 포함) */
    @Transactional
    public PromotionRequest reject(Long requestId, String reason) {
        PromotionRequest req = findPending(requestId);
        req.setStatus(Status.REJECTED);
        req.setHandledAt(LocalDateTime.now());
        sendUserNotification(req, false, reason);
        return req;
    }

    // 공통 헬퍼: PENDING 검증
    private PromotionRequest findPending(Long requestId) {
        PromotionRequest req = promoRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));
        if (req.getStatus() != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        return req;
    }

    /**
     * 승인/거절 결과를 요청자에게 메일로 통보
     * @param req      처리된 요청
     * @param approved true=승인, false=거절
     * @param reason   거절 사유 (approved=false 일 때만 사용)
     */
    private void sendUserNotification(PromotionRequest req, boolean approved, String reason) {
        User user = req.getUser();
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setTo(user.getEmail());

            if (approved) {
                helper.setSubject("[승격 승인] 관리자 권한이 부여되었습니다.");
                helper.setText(
                        "<p>안녕하세요, " + user.getNickname() + "님.</p>" +
                                "<p>요청하신 관리자 권한 부여가 <strong>승인</strong>되었습니다.</p>" +
                                "<p><a href=\"http://localhost:5173/team6/admin\">관리자 페이지로 이동</a></p>",
                        true
                );
            } else {
                helper.setSubject("[승격 거절] 관리자 승격 요청이 거절되었습니다.");
                StringBuilder sb = new StringBuilder();
                sb.append("<p>안녕하세요, ").append(user.getNickname()).append("님.</p>");
                sb.append("<p>요청하신 관리자 승격이 <strong>거절</strong>되었습니다.</p>");
                sb.append("<p>거절 사유: ").append(reason).append("</p>");
                sb.append("<p>문의사항이 있으시면 운영팀에 연락해 주세요.</p>");
                helper.setText(sb.toString(), true);
            }

            mailSender.send(msg);

        } catch (MessagingException e) {
            System.err.println("사용자 알림 메일 전송 실패: " + user.getEmail());
            e.printStackTrace();
        }
    }
}
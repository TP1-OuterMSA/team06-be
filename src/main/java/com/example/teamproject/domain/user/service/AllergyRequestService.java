package com.example.teamproject.domain.user.service;

import com.example.teamproject.domain.allergy.entity.Allergy;
import com.example.teamproject.domain.allergy.service.AllergyService;
import com.example.teamproject.domain.user.dto.request.AllergyRequestDto;
import com.example.teamproject.domain.user.entity.AllergyRequest;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.domain.user.repository.AllergyRequestRepository;
import com.example.teamproject.domain.userAllergy.service.UserAllergyService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyRequestService {

    private final AllergyRequestRepository allergyRepo;
    private final UserRepository userRepo;
    private final AllergyService allergyService;
    private final UserAllergyService userAllergyService;

    private final JavaMailSender mailSender;

    @Transactional
    public void requestAllergyAddition(Long userId, AllergyRequestDto dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        AllergyRequest req = AllergyRequest.builder()
                .user(user)
                .allergyName(dto.getAllergyName())
                .status(AllergyRequest.Status.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        allergyRepo.save(req);

        sendRequestEmailsToAdmins(req);
    }

    private void sendRequestEmailsToAdmins(AllergyRequest req) {
        List<User> allUsers = userRepo.findAll();
        List<User> admins = new ArrayList<>();
        for (User u : allUsers) {
            if ("ADMIN".equals(u.getRole())) {
                admins.add(u);
            }
        }

        for (User admin : admins) {
            try {
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");

                helper.setTo(admin.getEmail());
                helper.setSubject("[알레르기 추가 요청] " + req.getUser().getUsername());

                String loginUrl = "http://localhost:5173/team6/login";
                String html = ""
                        + "<p>안녕하세요, 관리자님.</p>"
                        + "<p>사용자 <strong>" + req.getUser().getUsername()
                        + "</strong>님이 새 알레르기 <strong>" + req.getAllergyName()
                        + "</strong> 추가를 요청했습니다.</p>"
                        + "<p style=\"text-align:center; margin:30px 0;\">"
                        + "  <a href=\"" + loginUrl + "\" "
                        + "     style=\"display:inline-block;padding:12px 24px;"
                        + "background-color:#4F46E5;color:#fff;text-decoration:none;"
                        + "border-radius:4px;font-weight:bold;\">"
                        + "    요청 처리하러 가기"
                        + "  </a>"
                        + "</p>"
                        + "<hr/><p style=\"font-size:0.85em;color:#666;\">"
                        + "이 메일은 발신 전용입니다."
                        + "</p>";

                helper.setText(html, true);
                mailSender.send(msg);
            } catch (MessagingException e) {
                System.err.println("알레르기 요청 메일 전송 실패 to " + admin.getEmail());
                e.printStackTrace();
            }
        }
    }

    @Transactional(readOnly = true)
    public List<AllergyRequest> listPendingRequests() {
        return allergyRepo.findByStatus(AllergyRequest.Status.PENDING);
    }

    /** ADMIN → 알레르기 요청 승인 */
    @Transactional
    public void approveAllergy(Long requestId) {
        AllergyRequest req = allergyRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        if (req.getStatus() != AllergyRequest.Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 1) 상태 업데이트
        req.setStatus(AllergyRequest.Status.APPROVED);
        req.setHandledAt(LocalDateTime.now());

        // 2) Allergy 테이블에 추가 (이름으로 조회 후 없으면 새로 생성)
        String name = req.getAllergyName();
        Allergy allergy = allergyService.getAllAllergies().stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> allergyService.addAllergy(name));

        // 3) 사용자-알레르기 매핑 저장
        userAllergyService.saveUserAllergy(req.getUser(), allergy.getId());

        // 4) 사용자에게 승인 메일 발송
        sendUserNotification(req, true,null);
    }

    /** ADMIN → 알레르기 요청 거절 */
    @Transactional
    public void rejectAllergy(Long requestId, String reason) {
        AllergyRequest req = allergyRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));
        if (req.getStatus() != AllergyRequest.Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        req.setStatus(AllergyRequest.Status.REJECTED);
        req.setHandledAt(LocalDateTime.now());

        sendUserNotification(req, false, reason);
    }

    /** 승인/거절 결과를 사용자에게 이메일로 통보 */
    private void sendUserNotification(AllergyRequest req, boolean approved,String reason) {
        User user = req.getUser();
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");

            helper.setTo(user.getEmail());
            if (approved) {
                helper.setSubject("[알레르기 추가 승인] 요청하신 '"
                        + req.getAllergyName() + "' 알레르기가 승인되었습니다.");
                helper.setText(
                        "<p>안녕하세요, " + user.getNickname() + "님.</p>" +
                                "<p>요청하신 알레르기 '<strong>" + req.getAllergyName() +
                                "</strong>' 추가가 <strong>승인</strong>되었습니다.</p>" +
                                "<p>마이페이지에서 <a href=\"http://localhost:5173/team6/mypage/allergies\">" +
                                "알레르기 정보를 확인</a>해 주세요.</p>",
                        true
                );
            } else {
                helper.setSubject("[알레르기 추가 거절] 요청하신 '" + req.getAllergyName() + "' 알레르기가 거절되었습니다.");
                StringBuilder sb = new StringBuilder();
                sb.append("<p>안녕하세요, ").append(user.getNickname()).append("님.</p>");
                sb.append("<p>요청하신 알레르기 '<strong>").append(req.getAllergyName()).append("</strong>' 추가가 <strong>거절</strong>되었습니다.</p>");
                sb.append("<p>거절 사유: ").append(reason).append("</p>");
                sb.append("<p>추가 문의사항이 있으시면 고객센터로 연락해 주세요.</p>");
                helper.setText(sb.toString(), true);
            }
            mailSender.send(msg);
        } catch (MessagingException e) {
            // 로깅
            System.err.println("사용자 알레르기 처리 알림 메일 전송 실패 to " + user.getEmail());
            e.printStackTrace();
        }
    }

}

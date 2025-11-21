package com.example.starlet_be.domains.friend.service;

import com.example.starlet_be.domains.friend.entity.Friend;
import com.example.starlet_be.domains.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.mail-url}")
    private String baseUrl;

    /**
     * 친구 요청 메일을 수신자에게 전송
     *
     * @param requester 친구 요청을 보낸 사용자
     * @param receiver  친구 요청을 받는 사용자
     * @param request   생성된 친구 요청 엔티티
     */
    @Transactional
    public void sendFriendRequestMail(User requester, User receiver, Friend request) {

        //프론트 친구요청 페이지로 이동. 지금은 그냥 starlet 홈 링크 걸어두기
        String link = baseUrl;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(receiver.getEmail().getAddress());
            helper.setSubject("[STARLET] 친구요청이 도착했습니다.");

            String html = buildFriendRequestHtml(requester, link);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("친구 요청 메일 전송 실패", e);
        }
    }

    /**
     * 친구 요청이 수락 -> 요청자에게 수락 알림 메일을 전송
     *
     * @param requester 친구 요청을 보낸 사용자
     * @param receiver  요청을 수락한 사용자
     */
    @Transactional
    public void sendFriendAcceptedMail(User requester, User receiver) {

        //프론트 친구목록 페이지로 이동. 지금은 그냥 starlet 홈 링크 걸어두기
        String link = baseUrl;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(requester.getEmail().getAddress());  // 신청을 보낸 사람에게 메일
            helper.setSubject("[STARLET] 친구요청이 수락되었습니다.");

            String html = buildFriendAcceptedHtml(receiver, link);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("친구 수락 메일 전송 실패", e);
        }
    }

    //친구 요청 메일 이미지
    private String buildFriendRequestHtml(User requester, String link) {
        return """
                <div style="font-family:Arial, sans-serif; padding:24px; background:#1B2735; color:white; border-radius:6px;">
                           \s
                            <h2 style="margin:0 0 12px 0; font-weight:700; font-size:24px;">STARLET</h2>
                           \s
                            <p style="margin:8px 0 5px 0; font-size:16px; line-height:1.5;">
                                <b>%s</b> 님이 친구 요청을 보냈습니다.
                            </p>
                
                            <p style="margin:0 0 18px 0; font-size:14px; color:#D0D0D0;">
                                아래 버튼을 눌러 Starlet에서 친구 요청을 확인해보세요.
                            </p>
                
                            <a href="%s" target="_blank"
                               style="
                                    display:inline-block;
                                    padding:10px 10px;
                                    background:#54C65B;
                                    color:white;
                                    font-size:14px;
                                    border-radius:6px;
                                    text-decoration:none;
                                    font-weight:600;">
                                친구 요청 보러 가기
                            </a>
                
                        </div>
            """.formatted(requester.getNickname(), link);
    }

    //친구 수락 메일 이미지
    private String buildFriendAcceptedHtml(User receiver, String link) {
        return """
                <div style="font-family:Arial, sans-serif; padding:24px; background:#1B2735; color:white; border-radius:6px;">
                
                            <h2 style="margin:0 0 12px 0; font-weight:700; font-size:24px;">STARLET</h2>
                
                            <p style="margin:8px 0 5px 0; font-size:16px; line-height:1.5;">
                                <b>%s</b> 님이 친구 요청을\s
                                <span style="color:#54C65B; font-weight:700;">수락</span>했습니다.
                            </p>
                            
                            <p style="margin:0 0 18px 0; font-size:14px; color:#D0D0D0;">
                                아래 버튼을 눌러 Starlet에서 친구 목록을 확인해보세요.
                            </p>
                
                            <a href="#" target="_blank"
                               style="
                                    display:inline-block;
                                    padding:10px 10px;
                                    background:#54C65B;
                                    color:white;
                                    font-size:14px;
                                    border-radius:6px;
                                    text-decoration:none;
                                    font-weight:600;">
                                친구 목록 보러 가기
                            </a>
                
                        </div>
                """.formatted(receiver.getNickname(), link);
    }
}


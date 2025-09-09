package com.example.starlet_be.domains.email.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.repository.EmailRepository;
import com.example.starlet_be.domains.email.resdto.EmailInfoDto;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
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
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Value("${app.frontend.base-url}")
    private String baseUrl;

    // 1. 이메일 추가
    @Transactional
    public Email createEmail(String address, Verify verify){
        if(existsEmailAddress(address))
            throw new CustomException(ErrorCode.EMAIL_CONFLICT);

        Email email = Email.builder()
                .address(address)
                .verify(verify)
                .build();
        return emailRepository.save(email);
    }

    // 2. 이메일 삭제
    @Transactional
    public void deleteEmail(Email email){
        emailRepository.delete(email);
    }

    // 3. 이메일 조회
    @Transactional(readOnly = true)
    public Email findEmailByAddress(String address){
        return emailRepository.findByAddress(address).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );
    }

    // 4. 이메일 중복 확인
    @Transactional(readOnly = true)
    public boolean existsEmailAddress(String address){
        return emailRepository.existsByAddress(address);
    }

    // 5. 이메일 인증 상태 조회
    @Transactional(readOnly = true)
    public EmailInfoDto getVerificationStatus(String address) {
        // 1. 해당 이메일 객체 조회
        Email email = findEmailByAddress(address);

        String expireTimeStr = null;
        if(email.getVerify().getExpireTime() != null){
            expireTimeStr = email.getVerify().getExpireTime().toString();
        }

        return EmailInfoDto.builder()
                .emailId(email.getId())
                .emailAddress(email.getAddress())
                .verifyType(email.getVerify().getType().toString())
                .verifyExpireAt(expireTimeStr)
                .build();
    }

    // 5. 계정 생성 후 첫 인증 메일 전송
    @Transactional
    public void sendVerificationEmail(Email email, String token){
        String link = baseUrl + "/api/v1/verify/init?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getAddress());
            helper.setSubject("StarLet 회원가입 이메일 인증을 완료해주세요");

            String html = """
                <html>
                  <body>
                    <p>안녕하세요</p>
                    <p>가입을 완료하려면 아래 버튼을 클릭해주세요:</p>
                    <p style="text-align:center;">
                      <a href="%s"
                         style="
                           background-color:#4CAF50;
                           color:white;
                           padding:10px 20px;
                           text-decoration:none;
                           border-radius:5px;
                         ">
                        이메일 인증하기
                      </a>
                    </p>
                    <p>버튼이 작동하지 않으면, 아래 토큰을 복사해서 브라우저에 붙여넣어 주세요:</p>
                    <p>%s</p>
                  </body>
                </html>
                """.formatted(link, token);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }


    // 5. 비밀번호 초기화 인증 메일 전송
    public void sendPasswordResetEmail(Email email, String token){
        String link = baseUrl + "/api/v1/verify/password-reset/confirm?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getAddress());
            helper.setSubject("StarLet 비밀번호 변경 인증 메일입니다.");

            String html = """
                <html>
                  <body>
                    <p>안녕하세요</p>
                    <p>비밀번호를 변경 인증을 원하시면 아래 버튼을 눌러주세요:</p>
                    <p style="text-align:center;">
                      <a href="%s"
                         style="
                           background-color:#4CAF50;
                           color:white;
                           padding:10px 20px;
                           text-decoration:none;
                           border-radius:5px;
                         ">
                        이메일 인증하기
                      </a>
                    </p>
                    <p>버튼이 작동하지 않으면, 아래 토큰을 복사해서 브라우저에 붙여넣어 주세요:</p>
                    <p>%s</p>
                  </body>
                </html>
                """.formatted(link, token);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("인증 메일 전송 실패", e);
        }
    }
}

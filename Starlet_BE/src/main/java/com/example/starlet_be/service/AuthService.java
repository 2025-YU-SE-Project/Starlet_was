package com.example.starlet_be.service;

import com.example.starlet_be.dto.PasswordResetConfirmDto;
import com.example.starlet_be.dto.PasswordResetReqDto;
import com.example.starlet_be.entity.Token;
import com.example.starlet_be.entity.User;
import com.example.starlet_be.entity.enums.TokenType;
import com.example.starlet_be.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JavaMailSender mailSender;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(User user, String token){
        String link = baseUrl + "/api/v1/auth/verify/email?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("StarLet 회원가입 이메일 인증을 완료해주세요");

            String html = """
                <html>
                  <body>
                    <p>안녕하세요, %s님!</p>
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
                """.formatted(user.getNickname(), link, token);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("인증 메일 전송 실패", e);
        }
    }

    public void sendPasswordResetEmail(User user, String token){
        String link = baseUrl + "/api/v1/auth/pw-reset/confirm?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("StarLet 비밀번호 변경 인증 메일입니다.");

            String html = """
                <html>
                  <body>
                    <p>안녕하세요, %s님!</p>
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
                """.formatted(user.getNickname(), link, token);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("인증 메일 전송 실패", e);
        }
    }

    public boolean emailVerification(String token){
        try{
            User user = tokenService.validateToken(token, TokenType.VERIFY);
            user.setVerified(true);
            userRepository.save(user);
            tokenService.deleteTokenByUser(user);
        } catch(IllegalArgumentException e){
            return false;
        }
        return true;
    }

    public boolean passwordResetVerification(String token){
        try{
            User user = tokenService.validateToken(token, TokenType.PASSWORD_RESET);
            user.setVerified(true);
            userRepository.save(user);
            tokenService.deleteTokenByUser(user);
        } catch(IllegalArgumentException e){
            return false;
        }
        return true;
    }

    // 비밀번호 변경 승인 요청
    public void requestNewPassword(PasswordResetReqDto dto){
        User user = userService.findByEmail(dto.getEmail());
        Token token = tokenService.createToken(user, TokenType.PASSWORD_RESET);
        sendPasswordResetEmail(user, token.getToken());
    }

    // 새로운 비밀번호 반영
    @Transactional
    public void updatePassword(PasswordResetConfirmDto dto){
        User user = tokenService.validateToken(dto.getToken(), TokenType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        tokenService.deleteTokenByUser(user);
    }
}

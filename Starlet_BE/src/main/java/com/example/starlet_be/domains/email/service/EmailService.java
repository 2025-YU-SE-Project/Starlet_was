package com.example.starlet_be.domains.email.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.repository.EmailRepository;
import com.example.starlet_be.domains.email.dto.EmailAddressDto;
import com.example.starlet_be.domains.email.dto.EmailInfoDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.repository.VerifyRepository;
import com.example.starlet_be.domains.verify.service.VerifyService;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 이메일(Email) 서비스
 *
 * 이메일 CRD, 중복확인, 인증상태조회, 가입 메일 전송, 비밀번호 초기화 메일 전송
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;
    private final VerifyService verifyService;
    private final UserRepository userRepository;
    private final VerifyRepository verifyRepository;

    @Value("${app.frontend.base-url}")
    private String baseUrl;

    /**
     * 이메일 생성
     *
     * 컨트롤러 단에서 불러오는게 아닌 서비스 내부에서 호출하는 메소드
     * 반드시 Verify 객체가 먼저 만들어진 뒤에 수행
     * 이메일이 이미 존재하면 EMAIL_CONFLICT
     *
     * @param address 이메일 주소
     * @param verify 인증 객체
     * @return Email 이메일 객체
     */
    @Transactional
    protected Email createEmail(String address, Verify verify){
        if(existsEmailAddress(address))
            throw new CustomException(ErrorCode.EMAIL_CONFLICT);

        Email email = Email.builder()
                .address(address)
                .verify(verify)
                .build();
        return emailRepository.save(email);
    }

    // 2. 이메일 삭제
//    @Transactional
//    protected void deleteEmail(Email email){
//        emailRepository.delete(email);
//    }

    /**
     * 이메일 조회
     *
     * 이메일 주소 기반 검색을 해서 없다면 EMAIL_NOT_FOUND
     *
     * @param address 이메일 주소
     * @return Email 찾은 객체 반환
     */
    @Transactional(readOnly = true)
    public Email findEmailByAddress(String address){
        return emailRepository.findByAddress(address).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );
    }

    /**
     * 이메일 중복 확인
     *
     * @param address 이메일 주소
     * @return boolean 이메일이 존재한다면 true, 아니면 false
     */
    @Transactional(readOnly = true)
    public boolean existsEmailAddress(String address){
        return emailRepository.existsByAddress(address);
    }

    /**
     * 이메일 인증상태 조회
     *
     * 조회되는 상태는
     *     VERIFY,
     *     EMAIL_VERIFICATION,
     *     REQUEST_PASSWORD_RESET,
     *     CHANGING_PASSWORD
     *
     * 인증 만료기간이 null인 경우도 존재하므로 그에 대한 방어도 구현하였음
     *
     * @param address 이메일 주소
     * @return EmailInfoDto -> 이메일id, 이메일주소, 인증상태, 인증만료날짜를 응답
     */
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

    /**
     * 초기 가입 인증 이메일 전송
     *
     * 사용가능한 이메일인 경우 해당 메소드가 실행되어 인증을 요구하게됨
     * 이메일을 재전송할 수 있게 아래와 같이 구현함
     *
     * @param dto 이메일 주소
     */
    @Transactional
    public void initEmail(EmailAddressDto dto){

        if(emailRepository.existsByAddress(dto.getEmail())){
            // 이미 가입되어있으면 방어
            if(userRepository.existsByEmailAddress(dto.getEmail()))
                throw new CustomException(ErrorCode.USER_ALREADY_EXIST);

            // 인증정보 불러오기
            Verify verify = verifyRepository.findByEmail_Address(dto.getEmail()).orElseThrow(
                    () -> new CustomException(ErrorCode.VERIFY_NOT_FOUND)
            );
            // 인증정보 최신화
            verify.updateStatus(
                    verifyService.createToken(),
                    VerifyType.EMAIL_VERIFICATION,
                    LocalDateTime.now().plusHours(8)
            );
            // 저장
            verifyRepository.save(verify);

            sendVerificationEmail(
                    findEmailByAddress(dto.getEmail()),
                    verify.getToken()
            );
        } else {
            // 인증 객체 최초 생성
            Verify verify = verifyService.createVerify();

            // 이메일 최초 생성 후 인증 이메일 전송
            sendVerificationEmail(
                    createEmail(dto.getEmail(), verify),
                    verify.getToken()
            );
        }
    }

    /**
     * 비밀번호 초기화 요청 인증 이메일 전송
     *
     * 사용자가 비밀번호를 잊어버렸을경우 인증 메일을 전송한다.
     * 해당 계정을 비밀번호 요청 상태로 변경한 후 메일을 전송한다.
     *
     * VerifyService에 의해서 인증 상태를 검사하니 해당 메소드를 따라갈 것.
     *
     * 이메일 기반으로 검색해서 사용자가 없을경우 USER_NOT_FOUND
     *
     * @param dto 이메일 주소
     */
    @Transactional
    public void requestPasswordReset(EmailAddressDto dto){
        // 1. 가입된 사용자 조회
        User user = userRepository.findByEmailAddress(dto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 이메일 조회
        Email email = findEmailByAddress(dto.getEmail());

        // 3. 해당 계정의 이메일의 인증상태를 바꿀 것
        verifyService.passwordResetRequestStatus(email);

        // 4. 재설정 이메일을 보낼 것
        sendPasswordResetEmail(email, email.getVerify().getToken());
    }

    /**
     * 인증 이메일 전송 메소드
     *
     * initEmail 메소드에 의해 실행되어야 하여 protected임
     *
     * 이메일 전송 자체를 실패하는 경우 EMAIL_SEND_FAILED 내부 서버 오류 응답을 보냄
     *
     * @param email 이메일 객체
     * @param token Verify 토큰 부분
     */
    @Transactional
    protected void sendVerificationEmail(Email email, String token){
        String link = baseUrl + "/view/v1/verify/init?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getAddress());
            helper.setSubject("✨ Starlet에 오신 것을 환영합니다! 별빛 여정을 시작해보세요.");

            String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <style> @import url('https://fonts.googleapis.com/css2?family=Gowun+Dodum&display=swap'); </style>
        </head>
        <body style="margin:0; padding:0; background-color:#000000;">
            <table width="100%%" border="0" cellpadding="0" cellspacing="0" background="cid:background-image" style="background-image:url('cid:background-image'); background-size:cover; background-position:center; background-color:#000000;">
                <tr>
                    <td align="center" style="padding: 50px 20px;">
                        <table width="100%%"  border="0" cellpadding="0" cellspacing="0" style="max-width: 600px; background-color: rgba(27, 39, 53, 0.8); border: 1px solid rgba(255, 255, 255, 0.2); border-radius: 20px; backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px); text-align: center; color: #ffffff; font-family: 'Gowun Dodum', sans-serif;">
                            <tr><td style="padding: 20px;"></td></tr>
                            <tr><td style="font-size: 38px; font-weight: bold; letter-spacing: 5px;">S t a r l e t</td></tr>
                            <tr><td style="height: 20px;"></td></tr>
                            <tr><td><img src="cid:star-icon" alt="Welcome Star" width="120" style="border-radius: 50%%;"></td></tr>
                            <tr><td style="height: 20px;"></td></tr>
                            <tr><td style="font-size: 24px; font-weight: bold; padding: 0 40px;">별의 세계에 오신 것을 환영합니다</td></tr>
                            <tr><td style="height: 15px;"></td></tr>
                            <tr><td style="font-size: 16px; line-height: 1.7; padding: 0 40px; color: #dddddd;">당신의 소중한 감정들이 밤하늘의 별이 될 준비를 마쳤습니다. <br>아래 버튼을 눌러 별자리로 향하는 마지막 관문을 통과해주세요.</td></tr>
                            <tr><td style="height: 40px;"></td></tr>
                            <tr><td><a href="%s" target="_blank" style="background-color: #9370DB; color: #ffffff; padding: 15px 35px; text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: bold; display: inline-block;">여정 시작하기</a></td></tr>
                            <tr><td style="padding: 30px;"></td></tr>
                            <tr><td style="font-size: 12px; color: #999999; border-top: 1px solid rgba(255, 255, 255, 0.2); padding: 20px 0;">© 2025 Starlet. All rights reserved.</td></tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;

            String html = htmlTemplate.replace("%s", link);
            helper.setText(html, true);

            helper.addInline("star-icon", new ClassPathResource("static/images/star-icon.png"));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }


    /**
     * 인증 이메일 전송 메소드2
     *
     * requestPasswordReset 메소드에 의해 실행되어야 하여 protected임
     *
     * 이메일 전송 자체를 실패하는 경우 EMAIL_SEND_FAILED 내부 서버 오류 응답을 보냄
     *
     * @param email 이메일 객체
     * @param token Verify 토큰 부분
     */
    protected void sendPasswordResetEmail(Email email, String token){
        String link = baseUrl + "/view/v1/verify/password-reset/confirm?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject("🔑 Starlet 비밀번호 재설정을 위한 안내입니다.");
            helper.setTo(email.getAddress());

            String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <style> @import url('https://fonts.googleapis.com/css2?family=Gowun+Dodum&display=swap'); </style>
        </head>
        <body style="margin:0; padding:0; background-color:#1B2735;">
            <table width="100%%" border="0" cellpadding="0" cellspacing="0" style="background-color:#1B2735;">
                <tr>
                    <td align="center" style="padding: 50px 20px;">
                        <table width="100%%" border="0" cellpadding="0" cellspacing="0" style="max-width: 600px; background-color: #2C3E50; border-radius: 20px; text-align: center; color: #ffffff; font-family: 'Gowun Dodum', sans-serif;">
                            <tr><td style="padding: 20px;"></td></tr>
                            <tr><td style="font-size: 38px; font-weight: bold; letter-spacing: 5px;">S t a r l e t</td></tr>
                            <tr><td style="height: 40px;"></td></tr>
                            <tr><td style="font-size: 24px; font-weight: bold; padding: 0 40px;">새로운 비밀의 문을 열 시간입니다</td></tr>
                            <tr><td style="height: 15px;"></td></tr>
                            <tr><td style="font-size: 16px; line-height: 1.7; padding: 0 40px; color: #dddddd;">잊혀진 별의 길을 다시 찾기 위한 요청을 받았습니다. <br>아래 버튼을 눌러 새로운 비밀번호를 설정하고, 당신의 우주로 다시 접속하세요.</td></tr>
                            <tr><td style="height: 40px;"></td></tr>
                            <tr><td><a href="%s" target="_blank" style="background-color: #4682B4; color: #ffffff; padding: 15px 35px; text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: bold; display: inline-block;">비밀번호 재설정</a></td></tr>
                            <tr><td style="padding: 30px;"></td></tr>
                            <tr><td style="font-size: 12px; color: #999999; border-top: 1px solid #4E5D6C; padding: 20px 0;">© 2025 Starlet. All rights reserved.</td></tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;

            String html = htmlTemplate.replace("%s", link);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("인증 메일 전송 실패", e);
        }
    }
}

package com.example.starlet_be.domains.email.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.repository.EmailRepository;
import com.example.starlet_be.domains.email.reqdto.EmailAddressDto;
import com.example.starlet_be.domains.email.resdto.EmailInfoDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.user.service.UserService;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.service.VerifyService;
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
     *
     * @param dto 이메일 주소
     */
    @Transactional
    public void initEmail(EmailAddressDto dto){
        // 인증 객체 최초 생성
        Verify verify = verifyService.createVerify();

        // 이메일 생성 후 인증객체 붙이기
        Email email = createEmail(dto.getEmail(), verify);

        // 인증 이메일 전송
        sendVerificationEmail(email, verify.getToken());
    }

    /**
     * 비밀번호 초기화 요청 인증 이메일 전송
     *
     * 사용자가 비밀번호를 잊어버렸을경우 인증 메일을 전송한다.
     * 해당 계정을 비밀번호 요청 상태로 변경한 후 메일을 전송한다.
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

package com.example.starlet_be.domains.email.controller;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.reqdto.EmailAddressDto;
import com.example.starlet_be.domains.email.service.EmailService;
import com.example.starlet_be.domains.user.service.UserService;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.service.VerifyService;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerifyService verifyService;

    // 1. 이메일 중복 확인
    @GetMapping("/check-duplication")
    public ResponseEntity<?> checkDuplication(@RequestParam String address){
        if(emailService.existsEmailAddress(address))
            throw new CustomException(ErrorCode.EMAIL_CONFLICT);
        return ResponseEntity.ok().build();
    }

    // 2. 초기 이메일 인증 전송
    @PostMapping("/init")
    public ResponseEntity<?> initEmail(@RequestBody EmailAddressDto dto){
        // 인증 객체 최초 생성
        Verify verify = verifyService.createVerify();

        // 이메일 생성 후 인증객체 붙이기
        Email email = emailService.createEmail(dto.getEmail(), verify);

        // 인증 이메일 전송
        emailService.sendVerificationEmail(email, verify.getToken());
        return ResponseEntity.ok().build();
    }


    // 3. 비밀번호 재설정 이메일 전송
    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody EmailAddressDto dto){
        Email email = emailService.findEmailByAddress(dto.getEmail());

        // 1. 해당 계정의 이메일의 인증상태를 바꿀 것
        verifyService.passwordResetRequestStatus(email);

        // 2. 재설정 이메일을 보낼 것
        emailService.sendPasswordResetEmail(email, email.getVerify().getToken());

        return ResponseEntity.ok().build();
    }

}

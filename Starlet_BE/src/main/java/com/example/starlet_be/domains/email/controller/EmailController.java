package com.example.starlet_be.domains.email.controller;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.reqdto.EmailAddressDto;
import com.example.starlet_be.domains.email.service.EmailService;
import com.example.starlet_be.domains.user.service.UserService;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.service.VerifyService;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if(userService.existEmail(address))
            throw new CustomException(ErrorCode.EMAIL_CONFLICT);
        return ResponseEntity.ok().build();
    }

    // 2. 초기 이메일 인증 전송
    @PostMapping("/init")
    public ResponseEntity<?> initEmail(@RequestBody EmailAddressDto dto){
        Verify verify = verifyService.createVerify(VerifyType.EMAIL_VERIFICATION);
        Email email = emailService.createEmail(dto.getEmail(), verify);
        emailService.sendVerificationEmail(email, verify.getToken());
        return ResponseEntity.ok().build();
    }


    // 3. 비밀번호 재설정 이메일 전송




    // 5. 비밀번호 재설정




}

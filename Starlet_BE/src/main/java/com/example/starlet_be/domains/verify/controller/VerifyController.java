package com.example.starlet_be.domains.verify.controller;

import com.example.starlet_be.domains.verify.reqdto.PasswordResetConfirmDto;
import com.example.starlet_be.domains.verify.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/verify")
@RequiredArgsConstructor
public class VerifyController {

    private final VerifyService verifyService;

    // 1. 가입 이메일 인증 받기
    // 3. 계정생성 이메일 인증 이후 인증된 계정으로 전환
    @GetMapping("/email")
    public ResponseEntity<?> emailVerification(@RequestParam String token){
        return (verifyService.emailVerification(token)) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }


    // 2. 비밀번호 변경 허용 인증
    // 4. 비밀번호 초기화 이메일 인증 이후 비밀번호 변경상태로 전환
    @GetMapping("/password")
    public ResponseEntity<?> passwordResetVerification(@RequestParam String token){
        return (verifyService.passwordResetVerification(token)) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }


    // 가입 인증 상태가 만료되면 이메일 객체와 인증 객체 삭제
    // 비밀번호 변경 요청이었다면 이거는 그냥 만료시킴



    // 6. 새로운 비밀번호 반영(새로운 비밀번호 생성)
    @PostMapping("/password-reset/new-password")
    public ResponseEntity<?> confirmChangePassword(@RequestBody PasswordResetConfirmDto dto){
        verifyService.updatePassword(dto);
        return ResponseEntity.ok().build();
    }


}

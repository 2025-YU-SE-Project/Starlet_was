package com.example.starlet_be.controller;

import com.example.starlet_be.dto.PasswordResetConfirmDto;
import com.example.starlet_be.dto.PasswordResetReqDto;
import com.example.starlet_be.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/verify/email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        return (authService.emailVerification(token)) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @GetMapping("/verify/password")
    public ResponseEntity<?> verifyPasswordReset(@RequestParam String token){
        return (authService.passwordResetVerification(token)) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetReqDto dto){
        authService.requestNewPassword(dto);
        return ResponseEntity.ok("비밀번호 재설정 링크가 전송되었습니다.");
    }

    @PostMapping("/password-reset/new-password")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody PasswordResetConfirmDto dto){
        authService.updatePassword(dto);
        return ResponseEntity.ok("비밀번호가 재설정 되었습니다.");
    }
}

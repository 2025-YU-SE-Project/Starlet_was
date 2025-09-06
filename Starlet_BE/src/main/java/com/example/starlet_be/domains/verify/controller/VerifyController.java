package com.example.starlet_be.domains.verify.controller;

import com.example.starlet_be.domains.verify.reqdto.PasswordResetConfirmDto;
import com.example.starlet_be.domains.verify.service.VerifyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/verify")
@RequiredArgsConstructor
public class VerifyController {

    private final VerifyService verifyService;

    // 1. 가입 이메일 인증 받기
    @GetMapping("/email")
    public ResponseEntity<?> emailVerification(@RequestParam String token){
        verifyService.emailVerification(token);
        return ResponseEntity.ok().build();
    }


    // 2. 비밀번호 변경 허용 인증 이후 비밀번호 변경상태로 전환
    @GetMapping("/password")
    public ResponseEntity<?> passwordResetVerification(@RequestParam String token){
        verifyService.passwordResetVerification(token);
        return ResponseEntity.ok().build();
    }


    // 3. 새로운 비밀번호 반영(새로운 비밀번호 생성)
    @PostMapping("/password-reset/new-password")
    public ResponseEntity<?> confirmChangePassword(@RequestBody PasswordResetConfirmDto dto){
        verifyService.updatePassword(dto);
        return ResponseEntity.ok().build();
    }


    // 가입 인증 상태가 만료되면 이메일 객체와 인증 객체 삭제
    // 비밀번호 변경 요청이었다면 이거는 그냥 만료시킴




//    @PostMapping("/reissue")
//    public ResponseEntity<?> reissue(HttpServletRequest request) {
//        String refreshToken = jwtUtil.extractRefreshTokenFromCookie(request);
//
//        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
//        }
//
//        String email = jwtUtil.getEmailFromToken(refreshToken);
//        String newAccessToken = jwtUtil.createAccessToken(email);
//
//        return ResponseEntity.ok().body(Map.of("accessToken", newAccessToken));
//    }
}

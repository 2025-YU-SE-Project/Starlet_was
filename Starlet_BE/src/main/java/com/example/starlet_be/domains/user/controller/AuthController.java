package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.user.reqdto.PasswordResetConfirmDto;
import com.example.starlet_be.domains.user.reqdto.PasswordResetReqDto;
import com.example.starlet_be.security.JwtUtil;
import com.example.starlet_be.domains.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request) {
        String refreshToken = jwtUtil.extractRefreshTokenFromCookie(request);

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String newAccessToken = jwtUtil.createAccessToken(email);

        return ResponseEntity.ok().body(Map.of("accessToken", newAccessToken));
    }


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

package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.user.reqdto.PasswordResetConfirmDto;
import com.example.starlet_be.domains.user.reqdto.PasswordResetReqDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auth API", description = "JWT 액세스 토큰 재발급, 이메일 인증 계정, 비밀번호 변경 관련 API 입니다.")
public interface AuthApi {

    ResponseEntity<?> reissue(HttpServletRequest request);


    ResponseEntity<?> verifyEmail(@RequestParam String token);


    ResponseEntity<?> verifyPasswordReset(@RequestParam String token);


    ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetReqDto dto);


    ResponseEntity<?> confirmPasswordReset(@RequestBody PasswordResetConfirmDto dto);


}

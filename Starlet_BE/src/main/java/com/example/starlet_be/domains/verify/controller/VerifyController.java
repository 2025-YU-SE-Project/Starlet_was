package com.example.starlet_be.domains.verify.controller;

import com.example.starlet_be.domains.verify.api.VerifyApi;
import com.example.starlet_be.domains.verify.dto.request.PasswordResetConfirmDto;
import com.example.starlet_be.domains.verify.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
public class VerifyController implements VerifyApi {

    private final VerifyService verifyService;

    // 새로운 비밀번호 반영(새로운 비밀번호 생성)
    @PostMapping("/password-reset/new-password")
    public ResponseEntity<?> confirmChangePassword(@RequestBody PasswordResetConfirmDto dto){
        verifyService.updatePassword(dto);
        return ResponseEntity.ok().build();
    }

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

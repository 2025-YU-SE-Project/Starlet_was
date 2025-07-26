package com.example.starlet_be.controller;

import com.example.starlet_be.dto.PasswordResetReqDto;
import com.example.starlet_be.entity.User;
import com.example.starlet_be.service.AuthService;
import com.example.starlet_be.service.TokenService;
import com.example.starlet_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TokenService tokenService;
    private final AuthService authService;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        return (authService.emailVerification(token)) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetReqDto dto){
        User user = userService.findByEmail(dto.getEmail());

    }
}

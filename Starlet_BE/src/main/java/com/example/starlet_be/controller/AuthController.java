package com.example.starlet_be.controller;

import com.example.starlet_be.service.AuthService;
import com.example.starlet_be.service.TokenService;
import com.example.starlet_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

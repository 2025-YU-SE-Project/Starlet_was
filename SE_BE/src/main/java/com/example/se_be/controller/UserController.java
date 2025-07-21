package com.example.se_be.controller;

import com.example.se_be.dto.UserReqDto;
import com.example.se_be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1. 사용자 조회


    // 2. 사용자들 조회


    // 3. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserReqDto dto, BindingResult bindingResult){
        // 반환 형태 조금 깔끔하게 할 필요 있을듯
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(bindingResult.getAllErrors());

        Long id = userService.signUp(dto);

        return (id != null) ?
                ResponseEntity.created(URI.create("/api/v1/user/" + id)).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일 또는 닉네임");
    }


    // 4. 로그인


    // 5. 사용자 삭제

    // 정도면 충분할 것 같습니다..!
}

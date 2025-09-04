package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.user.reqdto.SignUpDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User API", description = "회원 관련 API 입니다.")
public interface UserApi {

    ResponseEntity<?> getUser(@PathVariable Long id);


    ResponseEntity<?> getUserList();

    ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto dto);


    ResponseEntity<?> existEmail(@RequestParam String email);


    ResponseEntity<?> login(@RequestBody SignUpDto dto, HttpServletResponse res);


    ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails);


    ResponseEntity<?> logout(HttpServletResponse response);
}

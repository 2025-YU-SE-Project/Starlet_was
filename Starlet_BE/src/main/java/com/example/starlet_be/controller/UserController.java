package com.example.starlet_be.controller;

import com.example.starlet_be.dto.UserReqDto;
import com.example.starlet_be.dto.UserResDto;
import com.example.starlet_be.security.JwtTokenProvider;
import com.example.starlet_be.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // 1-A. 사용자 조회(관리자 전용)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        UserResDto info = userService.getUser(id);

        return (info != null) ?
                ResponseEntity.ok().body(info) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저가 존재하지 않습니다.");
    }


    // 2-A. 사용자들 조회(관리자 전용)
    @GetMapping
    public ResponseEntity<?> getUserList(){
        List<UserResDto> infos = userService.getUserList();
        return ResponseEntity.ok().body(infos);
    }


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

    // 3-1. 이메일 중복 확인만(Restful한지는 모름)
    @GetMapping("/signup/email/{email}")
    public ResponseEntity<?> existEmail(@PathVariable String email){
        // 존재하면 true, 존재하지 않으면 false.
        return (userService.existEmail(email)) ?
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다.") :
                ResponseEntity.ok().build();
    }

    // 3-2. 닉네임 중복 확인만(Restful한지는 모름)
    @GetMapping("/signup/nickname/{nickname}")
    public ResponseEntity<?> existNickname(@PathVariable String nickname){
        // 존재하면 true, 존재하지 않으면 false.
        return (userService.existNickname(nickname)) ?
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 닉네임입니다.") :
                ResponseEntity.ok().build();
    }

    // 닉네임 길이 확인이랑 비밀번호 길이는 프론트엔드에서 검사해도 괜찮을 듯 합니다.


    // JWT 토큰 방식은 인터넷을 참고하여 코딩
    // 4. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserReqDto dto,  HttpServletResponse res){
        // 1. 인증
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        authenticationManager.authenticate(token);

        // 2. 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(dto.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(dto.getEmail());

        // 3. Refresh 토큰 보호
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 하루
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 4. Access 토큰은 응답 바디나 헤더에 담기
        return ResponseEntity.ok().body(Map.of("accessToken", accessToken));
    }


    // 5. 사용자 삭제


    // 정도면 충분할 것 같습니다..!
}

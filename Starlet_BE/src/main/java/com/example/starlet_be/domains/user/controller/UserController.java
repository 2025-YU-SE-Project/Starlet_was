package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.user.reqdto.UserReqDto;
import com.example.starlet_be.domains.user.resdto.UserResDto;
import com.example.starlet_be.domains.user.entity.Token;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.entity.enums.TokenType;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.security.JwtUtil;
import com.example.starlet_be.domains.user.service.AuthService;
import com.example.starlet_be.domains.user.service.TokenService;
import com.example.starlet_be.domains.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;

    // 1-A. 사용자 조회(관리자 전용)
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        UserResDto info = userService.getUser(id);
        return ResponseEntity.ok().body(info);
    }


    // 2-A. 사용자들 조회(관리자 전용)
    @GetMapping("/get")
    public ResponseEntity<?> getUserList(){
        List<UserResDto> infos = userService.getUserList();
        return ResponseEntity.ok().body(infos);
    }


    // 3. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserReqDto dto, BindingResult bindingResult){
        // 반환 형태 조금 깔끔하게 할 필요 있을듯
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(bindingResult.getAllErrors());

        User user = userService.signUp(dto);
        if(user == null)
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        Token token = tokenService.createToken(user, TokenType.VERIFY);
        if(token == null)
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        authService.sendVerificationEmail(user, token.getToken());

        return ResponseEntity.created(URI.create("/api/v1/user/" + user.getId())).build();

    }

    // 3-1. 이메일 중복 확인만
    @GetMapping("/signup/email_available")
    public ResponseEntity<?> existEmail(@RequestParam String email){
        // 존재하면 true, 존재하지 않으면 false.
        if(userService.existEmail(email))
            throw new CustomException(ErrorCode.EMAIL_CONFLICT);
        else
            return ResponseEntity.ok().build();
    }

    // 3-2. 닉네임 중복 확인만
    @GetMapping("/signup/nickname_available")
    public ResponseEntity<?> existNickname(@RequestParam String nickname){
        // 존재하면 true, 존재하지 않으면 false.
        if(userService.existNickname(nickname))
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        else
            return ResponseEntity.ok().build();
    }

    // 닉네임 길이 확인이랑 비밀번호 길이는 프론트엔드에서 검사해도 괜찮을 듯 합니다.


    // JWT 토큰 방식은 인터넷을 참고하여 코딩
    // 4. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserReqDto dto,  HttpServletResponse res){

        // 0. 유저 찾기
        User user = userService.findByEmail(dto.getEmail());

        // 1. 인증
        try{
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            authenticationManager.authenticate(token);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOT_VERIFY_USER);
        }

        // 2. 토큰 발급
        String accessToken = jwtUtil.createAccessToken(dto.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(dto.getEmail());

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


    // 5. 사용자 삭제, URI는 임시
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        userService.deleteCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 6. 로그아웃 : 프론트엔드에서 그냥 토큰 삭제하기
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")              // 로그인과 동일하게
                .httpOnly(true)        // 동일하게
                .maxAge(0)             // 즉시 만료
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok().build();
    }



    // 정도면 충분할 것 같습니다..!
}

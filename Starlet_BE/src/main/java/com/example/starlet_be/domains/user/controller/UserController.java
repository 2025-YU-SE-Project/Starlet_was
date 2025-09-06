package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.service.EmailService;
import com.example.starlet_be.domains.user.reqdto.LoginDto;
import com.example.starlet_be.domains.user.reqdto.SignUpDto;
import com.example.starlet_be.domains.user.resdto.UserResDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.service.VerifyService;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.domains.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final EmailService emailService;

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

    // 3. 회원가입(이메일 인증이 끝났다는 가정하에 진행됨)
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto dto){

        // 인증된 이메일 가져오기
        Email email = emailService.findEmailByAddress(dto.getEmail());

        if(email.getVerify().getType() != VerifyType.VERIFY)
            throw new CustomException(ErrorCode.NOT_VERIFY_USER);

        User user = userService.signUp(dto, email);
        if(user == null)
            throw new CustomException(ErrorCode.USER_CREATE_FAILED);


        return ResponseEntity.created(URI.create("/api/v1/user/" + user.getId())).build();

    }


    // 3. 닉네임 중복 확인
    @GetMapping("/signup/nickname_available")
    public ResponseEntity<?> existNickname(@RequestParam String nickname){
        // 존재하면 true, 존재하지 않으면 false.
        if(userService.existNickname(nickname))
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        else
            return ResponseEntity.ok().build();
    }

    // JWT 토큰 방식은 인터넷을 참고하여 코딩
    // 4. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto dto, HttpServletResponse res){
        return ResponseEntity.ok().body(userService.login(dto, res));
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
}

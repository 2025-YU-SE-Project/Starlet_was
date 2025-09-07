package com.example.starlet_be.domains.user.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.repository.EmailRepository;
import com.example.starlet_be.domains.user.reqdto.LoginDto;
import com.example.starlet_be.domains.user.reqdto.SignUpDto;
import com.example.starlet_be.domains.user.resdto.LoginInfoDto;
import com.example.starlet_be.domains.user.resdto.UserResDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.repository.VerifyRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailRepository emailRepository;
    private final VerifyRepository verifyRepository;

    // 유저 단일 조회
    @Transactional(readOnly = true)
    public UserResDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        return user.toResDto();
    }

    // 유저 전체 조회
    @Transactional(readOnly = true)
    public List<UserResDto> getUserList() {
        List<User> users = userRepository.findAll();
        List<UserResDto> dtos = new ArrayList<>();

        for(User user : users)
            dtos.add(user.toResDto());

        return dtos;
    }

    // 회원가입
    @Transactional
    public User signUp(SignUpDto dto, Email email) {
        // 닉네임 및 이메일 중복 확인
        if(existNickname(dto.getNickname()))
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);

        return userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword()), email));
    }


    // 닉네임 존재(중복) 확인
    @Transactional(readOnly = true)
    public boolean existNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 로그인
    @Transactional
    public LoginInfoDto login(LoginDto dto, HttpServletResponse res) {
        // 1. 유저 찾기
        User user = userRepository.findByEmailAddress(dto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 비밀번호 확인
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);

        // 3. 이메일인증, 비밀번호 분실 계정이 아닌지 검증
        try{
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            authenticationManager.authenticate(token);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOT_VERIFY_USER);
        }

        // 4. JWT 토큰 발급
        String accessToken = jwtUtil.createAccessToken(dto.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(dto.getEmail());

        // 5. 리프레쉬 토큰 헤더에 붙이는 작업
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(7*24*60*60) // 일주일
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 6. DTO 구성 반환
        return LoginInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail().getAddress())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 로그인 되어있는 유저가 계정 삭제
    @Transactional
    public void deleteCurrentUser(String email) {
        User user = userRepository.findByEmailAddress(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Email userEmail = user.getEmail();
        Verify userVerify = userEmail.getVerify();

        userRepository.delete(user);
        emailRepository.delete(userEmail);
        verifyRepository.delete(userVerify);
    }

    // 이메일 기반 찾기
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmailAddress(email).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND) );
    }
}

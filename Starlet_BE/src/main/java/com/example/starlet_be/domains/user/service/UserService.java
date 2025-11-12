package com.example.starlet_be.domains.user.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.service.EmailService;
import com.example.starlet_be.domains.user.dto.LoginDto;
import com.example.starlet_be.domains.user.dto.LoginInfoDto;
import com.example.starlet_be.domains.user.dto.SignUpDto;
import com.example.starlet_be.domains.user.dto.UserResDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.repository.VerifyRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.openai.dto.ModerationDto;
import com.example.starlet_be.openai.service.ModerationService;
import com.example.starlet_be.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자(User) 관리 서비스
 * 회원가입, 로그인, 회원탈퇴, 로그아웃, 이메일 기반 검색, 닉네임 중복 확인
 * 로그아웃은 백엔드에 구현하지 않고 프론트엔드에서 토큰 삭제해주는 정도로 끝낼 예정
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerifyRepository verifyRepository;
    private final EmailService emailService;
    private final ModerationService moderationService;

    /**
     * 사용자 단일 조회
     *
     * ID 기반으로 사용자 정보를 가져온다.
     * 사용자가 존재하지 않는다면 USER_NOT_FOUND 예외 발생
     *
     * SRS에서 별도로 지정된 기능이 아니며 프론트엔드의 테스트 용도로 이용된다.
     * 사용 용도가 없다면 폐기예정
     *
     * @param id 사용자 ID
     * @return UserResDto 응답 DTO
     */
    @Transactional(readOnly = true)
    public UserResDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        return user.toResDto();
    }

    /**
     * 사용자 목록 조회
     *
     * 서비스를 이용중인 모든 사용자 정보를 가져온다.
     *
     * SRS에서 별도로 지정된 기능이 아니며 프론트엔드의 테스트 용도로 이용된다.
     * 사용 용도가 없다면 폐기예정
     *
     * @return List<UserResDto> 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<UserResDto> getUserList() {
        List<User> users = userRepository.findAll();
        List<UserResDto> dtos = new ArrayList<>();

        for(User user : users)
            dtos.add(user.toResDto());

        return dtos;
    }

    /**
     * 회원가입
     *
     * dto에 제대로 정보가 담기지 않는다면 400 응답
     * 이메일을 생성조차 하지 않으면(인증 메일 발송을 하지 않으면 객체가 없음) EMAIL_NOT_FOUND 예외 404 응답
     * 이메일 생성 후 인증하지 않으면 NOT_VERIFY_USER 예외 400 응답
     * 닉네임이 중복되거나 유해성을 포함하는 경우 각각 NICKNAME_CONFLICT, ErrorCode.INAPPROPRIATE_CONTENT 반환
     * @param dto 회원가입을 위한 기본정보들. 이메일, 닉네임, 비밀번호
     * @return User 만들어진 객체 반환
     */
    @Transactional
    public User signUp(SignUpDto dto) {
        // 인증된 이메일 가져오기
        Email email = emailService.findEmailByAddress(dto.getEmail());

        if(email.getVerify().getType() != VerifyType.VERIFY)
            throw new CustomException(ErrorCode.NOT_VERIFY_USER);

        // 닉네임 및 이메일 중복 확인 (대체 예정)
//        if(existNickname(dto.getNickname()))
//            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);

        // 닉네임 유효성(중복 및 유해성 검증)
        validNickname(dto.getNickname());

        return userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword()), email));

    }

    /**
     * 닉네임 유효성 확인
     *
     * 이미 존재하는 닉네임이 있으면 NICKNAME_CONFLICT 409 응답
     * 닉네임에 유해한 정보가 심하게 포함되어 있다면 INAPPROPRIATE_CONTENT 400 응답
     *
     * @param nickname
     * @return boolean 중복되면 true, 아니면 false
     */
    @Transactional(readOnly = true)
    public void validNickname(String nickname) {
        if(userRepository.existsByNickname(nickname))
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);

        ModerationDto.ModerationResponse moderationResponse = moderationService.moderate(nickname);

        if(moderationResponse == null || moderationResponse.getResults() == null)
            throw new CustomException(ErrorCode.OPENAI_SERVER_ERROR);

        if(moderationResponse.getResults().get(0).isFlagged())
            throw new CustomException(ErrorCode.INAPPROPRIATE_CONTENT);

    }

    /**
     * 로그인
     *
     * dto에 모든 정보가 담기지 않으면 400 응답
     * 가입된 사용자가 존재하지 않으면 USER_NOT_FOUND 응답
     * 비밀번호가 틀리면 INCORRECT_PASSWORD 응답
     * 인증상태가 비정상적일 경우 NOT_VERIFY_USER 응답
     * 가입된 모든 상태(비밀번호 변경 절차 포함)에서 로그인을 성공할경우 정상계정으로 변경
     * 프론트엔드 작업 끝나기전까지 헤더와 응답에 모두 AccessToken 및 RefreshToken 반영
     *
     * @param dto 로그인에 필요한 정보. 이메일 및 비밀번호
     * @param res 헤더
     * @return LoginInfoDto -> userId, email, nickname, 토큰 2개 응답
     */
    @Transactional
    public LoginInfoDto login(LoginDto dto, HttpServletResponse res) {
        // 1. 유저 찾기
        User user = userRepository.findByEmailAddress(dto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 비밀번호 확인
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);

        // 3. 인증상태 검증 - 초기화 요청했던 계정은 그냥 로그인 성공했으므로 철회, 나머지는 방어
        Verify verify = user.getEmail().getVerify();
        if(verify.getType() == VerifyType.REQUEST_PASSWORD_RESET
        || verify.getType() == VerifyType.CHANGING_PASSWORD){
            verify.updateStatus(null, VerifyType.VERIFY, null);
            verifyRepository.save(verify);
        }
        if(verify.getType() == VerifyType.EMAIL_VERIFICATION) {
            throw new CustomException(ErrorCode.NOT_VERIFY_USER);
        }

        // 4. JWT 토큰 발급
        String accessToken = jwtUtil.createAccessToken(dto.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(dto.getEmail());

        // 5. 리프레쉬 토큰 헤더에 붙이는 작업
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(7*24*60*60) // 일주일
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // 6. 액세스 토큰 헤더에 붙이는 작업
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(12*60*60) // 12시간
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        // 7. DTO 구성 반환
        return LoginInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail().getAddress())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 회원탈퇴
     *
     * 이메일 기반 조회했을때 사용자가 없다면 USER_NOT_FOUND 응답
     *
     * @param email
     */
    @Transactional
    public void deleteCurrentUser(String email) {
        User user = userRepository.findByEmailAddress(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    /**
     * 이메일 기반 조회
     *
     * 사용자가 없다면 USER_NOT_FOUND 응답
     *
     * @param email 이메일
     * @return User 찾은 엔티티 반환
     */
    @Transactional(readOnly = true)
    public User findByEmailAddress(String email) {
        return userRepository.findByEmailAddress(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    /**
     * 로그아웃
     *
     * 사실 프론트단에서 토큰 삭제해주는게 가장깔끔한 방법이라 형태만 구현하였습니다.
     *
     * @param res 헤더
     */
    @Transactional
    public void logout(HttpServletResponse res) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")              // 로그인과 동일하게
                .httpOnly(true)        // 동일하게
                .maxAge(0)             // 즉시 만료
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }
}

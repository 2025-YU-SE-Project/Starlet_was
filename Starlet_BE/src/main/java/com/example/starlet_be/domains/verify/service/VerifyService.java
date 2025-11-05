package com.example.starlet_be.domains.verify.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.repository.EmailRepository;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.repository.VerifyRepository;
import com.example.starlet_be.domains.verify.dto.PasswordResetConfirmDto;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 인증 서비스
 * 인증 객체 생성, 토큰 인증, 만료 인증객체 정리, 가입 이메일 인증 받기, 비밀번호 변경 인증 받기, 새로운 비밀번호 반영
 */
@Service
@RequiredArgsConstructor
public class VerifyService {

    private final VerifyRepository verifyRepository;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 토큰 문자열 랜덤 생성
     * @return String
     */
    public String createToken(){
        return UUID.randomUUID().toString();
    }

    /**
     * 토큰 객체 생성
     *
     * 이메일 인증이 필요한 상태로 세팅 EMAIL_VERIFICATION
     *
     * @return Verify
     */
    @Transactional
    public Verify createVerify(){
        String token = createToken();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(8);
        Verify verify = Verify.builder()
                .token(token)
                .type(VerifyType.EMAIL_VERIFICATION)
                .expireTime(expireTime)
                .build();
        return verifyRepository.save(verify);
    }

    /**
     * 토큰 인증
     *
     * 서비스 내 메소드에 의해 호출되므로 protected
     *
     * 인증 객체가 없다면 VERIFY_NOT_FOUND
     * 인자로 전달한 타입과 일치하지 않으면 VERIFY_TYPE_NOT_MATCHED
     *
     * @param token 토큰 문자열
     * @param type 확인을 해볼 타입
     * @return Verify
     */
    @Transactional
    protected Verify validateToken(String token, VerifyType type){
        Verify verify = verifyRepository.findByToken(token).orElseThrow(
                () -> new CustomException(ErrorCode.VERIFY_NOT_FOUND)
        );

        if(verify.getType() != type)
            throw new CustomException(ErrorCode.VERIFY_TYPE_NOT_MATCHED);
        return verify;
    }

    // 인증 상태 가져오기(관리자용)
//    @Transactional(readOnly = true)
//    public VerifyType getVerifyType(Email email){
//        return email.getVerify().getType();
//    }

    /**
     * 만료 인증객체 정리
     *
     * 가입 인증 상태가 만료되면 이메일 객체와 인증 객체 삭제
     * 비밀번호 변경 요청이었다면 이거는 그냥 만료시킴
     *
     */
    @Scheduled(cron = "0 */30 * * * *") // 30분마다 실행
    @Transactional
    public void cleanExpiredVerify(){
        List<Verify> expireList = verifyRepository.findAllByExpireTimeBefore(LocalDateTime.now());

        for(Verify verify : expireList){
            // 가입 이메일 인증 조차 안할경우
            if(verify.getType() == VerifyType.EMAIL_VERIFICATION){
                emailRepository.delete(verify.getEmail());
                verifyRepository.delete(verify);
            }
            // 비밀번호 초기화 요청을 받지 않아 취소되는 경우
            else if(verify.getType() == VerifyType.REQUEST_PASSWORD_RESET){
                verify.updateStatus(null, VerifyType.VERIFY, null);
                verifyRepository.save(verify);
            }
            // 상태가 정상인 계정이거나 새 비밀번호를 입력해야 하는 경우엔 인증 만료기간이 null 이므로 해당 문제가 발생할 수 없음
            else{
                verify.updateStatus(null, verify.getType(), null);
                verifyRepository.save(verify);
            }
        }
    }

    /**
     * 가입 이메일 인증 받기
     *
     * 인증 객체를 얻어와서 상태를 업데이트
     *
     * @param token 메일로 받은 토큰정보로 검증
     */
    @Transactional
    public void emailVerification(String token) {
        Verify verify = validateToken(token, VerifyType.EMAIL_VERIFICATION);
        verify.updateStatus(null, VerifyType.VERIFY, null);
        verifyRepository.save(verify);
    }

    /**
     * 비밀번호 변경 요청에 따른 상태변환
     *
     * VERIFY -> REQUEST_PASSWORD_RESET
     *
     * 가입된 계정 범위 안에서 초기화 이메일을 계속 전송할 수 있음.
     *
     * @param email 이메일 객체
     */
    @Transactional
    public void passwordResetRequestStatus(Email email){

        // 1. 이메일의 인증정보 가져오기
        Verify verify = email.getVerify();

        // 2. 가입 된 계정이 아니라면
        if(verify.getType() == VerifyType.EMAIL_VERIFICATION)
            throw new CustomException(ErrorCode.VERIFY_TYPE_NOT_MATCHED);

        // 3. 비밀번호 초기화 요청 상태로 변경과, 인증 유효 토큰 부여
        verify.updateStatus(createToken(), VerifyType.REQUEST_PASSWORD_RESET, LocalDateTime.now().plusHours(24));

        // 4. 해당 인증정보를 저장
        verifyRepository.save(verify);
    }

    /**
     * 인증 후 비밀번호 변경 가능 상태로 전환
     *
     * REQUEST_PASSWORD_RESET -> CHANGING_PASSWORD
     *
     * @param token 메일로 받은 토큰 문자열
     */
    @Transactional
    public void passwordResetVerification(String token) {
        // 1. 토큰 문자열과 인증상태를 기반으로 인증 정보 가져오기
        Verify verify = validateToken(token, VerifyType.REQUEST_PASSWORD_RESET);

        // 2. 새 비밀번호를 받을 준비가 되어있는 인증정보로 변경
        verify.updateStatus(null, VerifyType.CHANGING_PASSWORD, null);

        // 3. 인증정보 저장
        verifyRepository.save(verify);
    }

    /**
     * 새로운 비밀번호 반영
     *
     * 비밀번호를 반영 후 인증정보를 VERIFY로 원상복구함
     *
     * 이메일이 없다면 EMAIL_NOT_FOUND
     * CHANGING_PASSWORD 상태가 아닌데 접근한다면 VEIRFY_TYPE_NOT_MATCHED
     * 사용자가 없다면 USER_NOT_FOUND -> 사실 이메일이 없다면 발생하지 않을 예외이긴함
     *
     * @param dto 이메일과 새로운 비밀번호
     */
    @Transactional
    public void updatePassword(PasswordResetConfirmDto dto) {
        // 이메일이 맞는지 확인하고 새 비밀번호 암호화하여 넣기
        Email email = emailRepository.findByAddress(dto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );

        // 비밀번호 변경중인 계정인지 확인
        if(email.getVerify().getType() != VerifyType.CHANGING_PASSWORD)
            throw new CustomException(ErrorCode.VERIFY_TYPE_NOT_MATCHED);

        // 사용자도 가져오기
        User user = userRepository.findByEmailAddress(dto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 비밀번호 암호화 하여 저장
        user.changePassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // 인증정보도 승인상태로 바꿔주기
        Verify verify = email.getVerify();
        verify.updateStatus(null, VerifyType.VERIFY, null);
        verifyRepository.save(verify);
    }

}

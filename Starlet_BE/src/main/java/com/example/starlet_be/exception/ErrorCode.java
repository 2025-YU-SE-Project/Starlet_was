package com.example.starlet_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 유저 관련
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    INCORRECT_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    EMAIL_CONFLICT(409, "이메일이 중복됩니다."),
    NICKNAME_CONFLICT(409, "닉네임이 중복됩니다."),
    DUPLICATE_INFO_CONFLICT(409, "이미 사용중인 정보가 있습니다. 이메일과 닉네임 중복검사를 시행하세요."),
    USER_CREATE_FAILED(500, "유저 생성에 실패하였습니다."),
    VERIFY_TOKEN_CREATE_FAILD(500, "계정인증 토큰 생성에 실패하였습니다."),


    // 인증 관련
    EMAIL_SEND_FAILED(500, "메일 전송을 실패하였습니다."),
    NOT_VERIFY_USER(400, "이메일 미인증 / 비밀번호 초기화 중인 유저입니다."),


    // 기타 관련
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류입니다.");

    private final int status;
    private final String message;
}

package com.example.starlet_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련
    NOT_VERIFY_USER(400, "이메일 미인증 / 비밀번호 재설정 중인 유저입니다."),
    VERIFY_TYPE_NOT_MATCHED(400, "인증 상태 타입이 일치하지 않습니다."),
    VERIFY_NOT_FOUND(404, "인증 정보를 찾을 수 없습니다."),


    // 이메일 관련
    EMAIL_NOT_FOUND(404, "해당 이메일은 존재하지 않습니다."),
    EMAIL_CONFLICT(409, "이메일이 중복됩니다."),
    EMAIL_SEND_FAILED(500, "메일 전송을 실패하였습니다."),


    // 유저 관련
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    INCORRECT_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    NICKNAME_CONFLICT(409, "닉네임이 중복됩니다."),
    DUPLICATE_INFO_CONFLICT(409, "이미 사용중인 정보가 있습니다. 이메일과 닉네임 중복검사를 시행하세요."),
    USER_CREATE_FAILED(500, "유저 생성에 실패하였습니다."),

    // diary 관련
    DIARY_ALREADY_EXISTS(409, "해당 날짜에는 이미 감정 일기가 존재합니다."),
    DIARY_NOT_FOUND(404, "해당 날짜의 감정 일기를 찾을 수 없습니다."),
    DIARY_INVALID_PARAM(400, "요청 값이 올바르지 않습니다."),
    DIARY_INVALID_MONTH(400, "month는 1~12 사이여야 합니다."),


    // 별 관련
    DATE_PARSE_ERROR(400, "날짜 문자열 입력이 유효하지 않습니다."),
    STAR_NOT_FOUND(404, "해당 별을 찾을 수 없습니다."),
    STAR_POSITION_OUT_OF_SCOPE(400, "입력된 좌표가 범위 밖입니다."),

    // 기타 관련
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류입니다.");

    private final int status;
    private final String message;
}

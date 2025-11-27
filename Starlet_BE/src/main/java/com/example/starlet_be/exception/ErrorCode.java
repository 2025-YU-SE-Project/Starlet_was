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
    JWT_TOKEN_PARSING_ERROR(500, "JWT 필터링 오류가 발생하였습니다."),


    // 이메일 관련
    EMAIL_NOT_FOUND(404, "해당 이메일은 존재하지 않습니다."),
    EMAIL_CONFLICT(409, "이메일이 중복됩니다."),
    EMAIL_SEND_FAILED(500, "메일 전송을 실패하였습니다."),


    // 유저 관련
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    INCORRECT_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXIST(409, "해당 이메일로 가입한 사용자가 이미 존재합니다."),
    NICKNAME_CONFLICT(409, "닉네임이 중복됩니다."),
    DUPLICATE_INFO_CONFLICT(409, "이미 사용중인 정보가 있습니다. 이메일과 닉네임 중복검사를 시행하세요."),
    USER_CREATE_FAILED(500, "유저 생성에 실패하였습니다."),
    INVALID_NICKNAME(400, "사용할 수 없는 닉네임입니다."),

    // diary 관련
    DIARY_ALREADY_EXISTS(409, "해당 날짜에는 이미 감정 일기가 존재합니다."),
    DIARY_NOT_FOUND(404, "해당 날짜의 감정 일기를 찾을 수 없습니다."),
    DIARY_INVALID_MONTH(400, "month는 1~12 사이여야 합니다."),


    // 별 관련
    DATE_PARSE_ERROR(400, "날짜 문자열 입력이 유효하지 않습니다."),
    STAR_NOT_FOUND(404, "해당 별을 찾을 수 없습니다."),
    STAR_POSITION_OUT_OF_SCOPE(400, "입력된 좌표가 범위 밖입니다."),


    // 별자리 관련
    CONSTELLATION_POSITION_OUT_OF_SCOPE(400, "입력된 좌표가 범위 밖입니다."),
    CONSTELLATION_NOT_FOUND(404, "해당 별자리를 찾을 수 없습니다."),
    ALREADY_BELONG_TO_CONSTELLATION(409, "이미 별자리에 소속된 별이 존재합니다."),


    // OpenAI 관련
    INAPPROPRIATE_CONTENT(400, "입력 내용에 부적절한 내용이 포함되었습니다."),
    OPENAI_SERVER_ERROR(500, "외부 서버(OpenAI) 오류입니다."),

    //친구 관련
    FRIEND_ALREADY_EXIST(409, "이미 친구 관계입니다."),
    FRIEND_REQUEST_ALREADY_PENDING(400, "이미 처리 중인 친구 요청이 있습니다."),
    FRIEND_REQUEST_NOT_FOUND(404, "유효한 친구 요청을 찾을 수 없습니다."),
    FRIEND_NOT_FOUND(404, "친구 관계를 찾을 수 없습니다."),
    FRIEND_REQUEST_EXPIRED(400, "친구 요청 유효 시간이 만료되었습니다."),
    CANNOT_REQUEST_SELF(400, "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    CANNOT_SEARCH_SELF(400, "자기 자신은 검색할 수 없습니다."),
    FORBIDDEN(403, "권한이 없습니다."),


    // 기타 관련
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류입니다.");

    private final int status;
    private final String message;
}

package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.user.dto.LoginDto;
import com.example.starlet_be.domains.user.dto.SignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "회원 ID 조회", description = "회원 고유 ID를 통한 회원 조회입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "id": 31,
                                        "nickname": "달나라 토끼",
                                        "email": "moonrabit25@naver.com"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "토큰 만료 혹은 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 401,
                                        "message": "토큰이 없거나 만료되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 유저를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getUser(@PathVariable Long id);


    @Operation(summary = "회원 목록 조회", description = "모든 회원들의 정보를 조회하는 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "id": 30,
                                            "nickname": "죽어볼태양",
                                            "email": "die4sun@naver.com"
                                        },
                                        {
                                            "id": 31,
                                            "nickname": "달나라 토끼",
                                            "email": "moonrabit25@naver.com"
                                        }
                                    ]
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "토큰 만료 혹은 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 401,
                                        "message": "토큰이 없거나 만료되었습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserList();


    @Operation(summary = "회원가입", description = "서비스를 이용하기 위한 회원가입 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입은 성공, 인증 따로 필요."),
            @ApiResponse(responseCode = "400", description = "입력 누락 및 형식 비일치",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "필드 누락", value = """
                                    {
                                        "<field>" : "<field>는 필수 입력입니다."
                                    }
                                    """),
                            @ExampleObject(name = "이메일 형식 비일치", value = """
                                    {
                                        "email": "이메일 형식을 맞춰주세요."
                                    }
                                    """),
                            @ExampleObject(name = "닉네임 길이 초과", value = """
                                    {
                                        "nickname": "닉네임은 최소 2글자, 최대 10글자 까지 가능합니다."
                                    }
                                    """),
                            @ExampleObject(name = "인증 되지 않은 계정", value = """
                                    {
                                        "status": 400,
                                        "message": "이메일 미인증 / 비밀번호 초기화 중인 유저입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "이메일 중복검사를 하고 인증메일을 발송하지 않은 경우, DB에 등록되지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 이메일은 존재하지 않습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "중복으로 인한 회원가입 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "이미 사용중인 정보가 있습니다. 이메일과 닉네임 중복검사를 시행하세요."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto dto);


    @Operation(summary = "사용가능한 닉네임 검사", description = "회원가입을 위한 닉네임 검사 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용가능한 닉네임"),
            @ApiResponse(responseCode = "400", description = "닉네임에 유해성 정보 포함 확인",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "입력 내용에 부적절한 내용이 포함되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "닉네임이 중복됩니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> existNickname(@RequestParam String nickname);


    @Operation(summary = "로그인", description = "로그인을 하는 기능이며, 여기서 나온 토큰으로 서비스에서 유저 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "userId": "<userId>",
                                        "email": "<email>",
                                        "nickname": "<nickname>",
                                        "accessToken": "<accessToken>",
                                        "refreshToken": "<refreshToken>"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 유저를 찾을 수 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "입력 누락 및 형식 비일치",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "필드 누락", value = """
                                    {
                                        "<field>" : "<field>는 필수 입력입니다."
                                    }
                                    """),
                            @ExampleObject(name = "이메일 미인증 / 비밀번호 초기화 중인 계정 차단(예방용)", value = """
                                    {
                                        "status": 400,
                                        "message": "이메일 미인증 / 비밀번호 재설정 중인 유저입니다."
                                    }
                                    """),
                            @ExampleObject(name = "이메일 형식 비일치", value = """
                                    {
                                        "email": "이메일 형식을 맞춰주세요."
                                    }
                                    """),
                            @ExampleObject(value = """
                                    {
                                        "status" : 400,
                                        "message" : "비밀번호가 일치하지 않습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> login(@RequestBody LoginDto dto, HttpServletResponse res);


    @Operation(summary = "회원탈퇴(삭제)", description = "회원탈퇴를 하는 기능입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status" : 401,
                                        "message" : "토큰이 없거나 만료되었습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "로그아웃", description = "로그아웃을 하는 기능입니다. 일단 해당 API는 명목상 만들어졌습니다. 로그아웃 한다면 프론트엔드에서 해당 토큰을 지워주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status" : 401,
                                        "message" : "토큰이 없거나 만료되었습니다."
                                    }
                                    """),
                    }))
    })
    ResponseEntity<?> logout(HttpServletResponse response);
}

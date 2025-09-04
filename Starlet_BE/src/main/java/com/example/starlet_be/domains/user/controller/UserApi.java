package com.example.starlet_be.domains.user.controller;

import com.example.starlet_be.domains.user.reqdto.SignUpDto;
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

    ResponseEntity<?> getUser(@PathVariable Long id);


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
//                            @ExampleObject(name = "닉네임 길이 초과", value = """
//                                    {
//                                        "nickname": "닉네임은 최대 10글자 까지 가능합니다."
//                                    }
//                                    """)
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


    @Operation(summary = "사용가능한 이메일 검사", description = "회원가입을 위한 이메일 검사 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용가능한 이메일"),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "이메일이 중복됩니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> existEmail(@RequestParam String email);


    @Operation(summary = "사용가능한 닉네임 검사", description = "회원가입을 위한 닉네임 검사 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용가능한 닉네임"),
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


    ResponseEntity<?> login(@RequestBody SignUpDto dto, HttpServletResponse res);


    ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails);


    ResponseEntity<?> logout(HttpServletResponse response);
}

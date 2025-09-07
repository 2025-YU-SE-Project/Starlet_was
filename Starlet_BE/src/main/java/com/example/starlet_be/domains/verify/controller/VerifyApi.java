package com.example.starlet_be.domains.verify.controller;

import com.example.starlet_be.domains.verify.reqdto.PasswordResetConfirmDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Verify API", description = "인증 및 보안에 관련된 API 입니다.")
public interface VerifyApi {

    @Operation(summary = "가입 이메일 인증 링크", description = "이메일로 받는 가입 인증 링크 입니다. 실제로는 메일로 테스트 하시면 됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증됨"),
            @ApiResponse(responseCode = "400", description = "인증 상태 타입이 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "인증 상태 타입이 일치하지 않습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "인증 객체 자체가 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "인증 정보를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> emailVerification(@RequestParam String token);



    @Operation(summary = "비밀번호 초기화 요청 이메일 인증 링크", description = "이메일로 받는 비밀번호 초기화 인증 링크 입니다. 실제로는 메일로 테스트 하시면 됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증됨"),
            @ApiResponse(responseCode = "400", description = "인증 상태 타입이 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "인증 상태 타입이 일치하지 않습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "인증 객체 자체가 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "인증 정보를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> passwordResetVerification(@RequestParam String token);


    ResponseEntity<?> confirmChangePassword(@RequestBody PasswordResetConfirmDto dto);
}

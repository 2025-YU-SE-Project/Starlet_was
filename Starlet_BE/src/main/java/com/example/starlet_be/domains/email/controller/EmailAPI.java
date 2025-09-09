package com.example.starlet_be.domains.email.controller;

import com.example.starlet_be.domains.email.reqdto.EmailAddressDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Email API", description = "이메일 정보, 메일 관련된 API 입니다.")
public interface EmailAPI {


    @Operation(summary = "이메일 주소 중복확인", description = "이미 가입되어있거나 DB에 존재하는 이메일 주소가 있는지 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 중복되지 않음"),
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
    ResponseEntity<?> checkDuplication(@RequestParam String address);


    @Operation(summary = "이메일 정보 확인",
            description = "이메일에 속한 정보를 조회합니다. 인증상태는 VERIFY, EMAIL_VERIFICATION, " +
                    "REQUEST_PASSWORD_RESET, CHANGING_PASSWORD가 있으며 날짜는 null를 허용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "emailId": 3,
                                        "emailAddress": "starlet2025@gmail.com",
                                        "verifyType": "VERIFY",
                                        "verifyExpireAt": null
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "이메일 정보 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 이메일은 존재하지 않습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getVerificationStatus(@RequestParam String address);



    @Operation(summary = "가입가능 이메일 인증메일 발송", description = "가입할 이메일 주소에 대해 인증메일을 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 발송 성공"),
            @ApiResponse(responseCode = "409", description = "이메일 발송 실패(해당 문제가 뜬다면, 백엔드에 문의할 것)",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 500,
                                        "message": "메일 전송을 실패하였습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> initEmail(@RequestBody EmailAddressDto dto);





    @Operation(summary = "비밀번호 변경 요청 인증메일 발송", description = "비밀번호 초기화를 위한 인증메일을 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 발송 성공"),
            @ApiResponse(responseCode = "409", description = "이메일 발송 실패(해당 문제가 뜬다면, 백엔드에 문의할 것)",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 500,
                                        "message": "메일 전송을 실패하였습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> requestPasswordReset(@RequestBody EmailAddressDto dto);
}

package com.example.starlet_be.domains.verify.api;

import com.example.starlet_be.domains.verify.dto.request.PasswordResetConfirmDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Verify API", description = "인증 및 보안에 관련된 API 입니다.")
public interface VerifyApi {

    @Operation(summary = "새 비밀번호 반영", description = "새로운 비밀번호로 최종적으로 변경하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "인증 상태 타입이 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "인증 상태 타입이 일치하지 않습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이메일 정보가 없음", value = """
                                    {
                                        "status": 404,
                                        "message": "해당 이메일은 존재하지 않습니다."
                                    }
                                    """),
                            @ExampleObject(name = "사용자가 존재하지 않음", value = """
                                    {
                                        "status": 404,
                                        "message": "해당 유저를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> confirmChangePassword(@RequestBody PasswordResetConfirmDto dto);
}

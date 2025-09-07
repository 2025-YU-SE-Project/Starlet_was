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






    ResponseEntity<?> initEmail(@RequestBody EmailAddressDto dto);






    ResponseEntity<?> requestPasswordReset(@RequestBody EmailAddressDto dto);
}

package com.example.starlet_be.domains.mypage.api;

import com.example.starlet_be.domains.constellation.dto.response.StarryNightConstellationDto;
import com.example.starlet_be.domains.mypage.dto.request.ConfirmPhotoReqDto;
import com.example.starlet_be.domains.mypage.dto.request.UpdateNicknameReqDto;
import com.example.starlet_be.domains.mypage.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MyPage API", description = "마이페이지 요약/레벨/대표 별자리/통계/프로필 수정 API")
public interface MyPageApi {

    @Operation(
            summary = "마이페이지 요약 정보 조회",
            description = """
                    사용자 프로필, 레벨, 대표 별자리,
                    연간 월별 별자리 수, 월별 감정 통계를 한 번에 조회합니다.
                    year, month가 없으면 현재 연/월 기준으로 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyPageSummaryResDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "profile": {
                                        "nickname": "starlet_user",
                                        "totalStars": 12,
                                        "totalConstellations": 3
                                        "profilePhotoUrl": "https://..."
                                      },
                                      "level": {
                                        "code": "STARLIGHT_EXPLORER",
                                        "name": "별빛 탐험가",
                                        "min": 0,
                                        "max": 9,
                                        "progressToNext": 9
                                      },
                                      "representativeConstellation": {
                                               "constellationId": 1,
                                               "userId": 1,
                                               "x": 0.69485012298182,
                                               "y": 0.6546418785565566,
                                               "name": "대표별자리",
                                               "createAt": "2025-11-21",
                                               "belongDate": "2025-11-21",
                                               "stars": [
                                                   {
                                                       "starId": 9,
                                                       "userId": 1,
                                                       "color": "YELLOW",
                                                       "date": "2025-11-21",
                                                       "x": 0.69485012298182,
                                                       "y": 0.6546418785565566
                                                   },
                                                   {
                                                       "starId": 10,
                                                       "userId": 1,
                                                       "color": "ORANGE",
                                                       "date": "2025-11-21",
                                                       "x": 0.69485012298182,
                                                       "y": 0.6546418785565566
                                                   }
                                               ],
                                               "connections": [
                                                   {
                                                       "connectionId": 1,
                                                       "startStarId": 9,
                                                       "endStarId": 14
                                                   }
                                               ]
                                           },
                                      "monthlyConstellations": [
                                        { "month": 1, "count": 0 },
                                        { "month": 2, "count": 1 }
                                      ],
                                      "emotionCounts": [
                                        { "emotion": "HAPPINESS", "count": 3 },
                                        { "emotion": "SADNESS", "count": 1 }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @GetMapping("/summary")
    ResponseEntity<MyPageSummaryResDto> getSummary(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    );



    @Operation(
            summary = "사용자 프로필 요약 조회",
            description = "닉네임, 총 별 개수, 총 별자리 개수, 프로필 url을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSummaryResDto.class))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @GetMapping("/user")
    ResponseEntity<UserSummaryResDto> getUserSummary(
            @AuthenticationPrincipal UserDetails principal
    );

    @Operation(
            summary = "사용자 레벨 조회",
            description = "보유한 총 별 개수를 기반으로 레벨 및 점수 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LevelResDto.class))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @GetMapping("/level")
    ResponseEntity<LevelResDto> getLevel(
            @AuthenticationPrincipal UserDetails principal
    );



    @Operation(
            summary = "대표 별자리 조회",
            description = "사용자가 설정한 대표 별자리를 조회합니다. 없으면 204(No Content)를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대표 별자리 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StarryNightConstellationDto.class))),
            @ApiResponse(responseCode = "204", description = "대표 별자리 없음"),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @GetMapping("/representative")
    ResponseEntity<?> getRepresentative(
            @AuthenticationPrincipal UserDetails principal
    );



    @Operation(
            summary = "연간 월별 별자리 수 통계 조회",
            description = """
                    year 기준으로 1~12월 각 월에 생성된 별자리 개수를 조회합니다.
                    예: GET /api/v1/mypage/year?year=2025
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MonthlyCountResDto.class),
                            examples = @ExampleObject(value = """
                                    [
                                      { "month": 1, "count": 0 },
                                      { "month": 2, "count": 1 },
                                      { "month": 3, "count": 3 }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @GetMapping("/year")
    ResponseEntity<List<MonthlyCountResDto>> getMonthlyCount(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) Integer year
    );

    @Operation(
            summary = "월별 감정 통계 조회",
            description = """
                    year, month 기준으로 해당 월에 작성된 일기들의 감정별(HAPPINESS, SADNESS 등) 개수를 조회합니다.
                    
                    - year : 4자리 연도 형식 (2025)
                    - month : 1~12 범위 정수
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmotionCountResDto.class),
                            examples = @ExampleObject(value = """
                                    [
                                      { "emotion": "HAPPINESS", "count": 3 },
                                      { "emotion": "SADNESS", "count": 1 },
                                      { "emotion": "NEUTRAL", "count": 0 }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "400", description = "파라미터 형식/범위 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 400, "message": "year 또는 month 파라미터가 올바르지 않습니다." }
                                    """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @GetMapping("/month")
    ResponseEntity<List<EmotionCountResDto>> getEmotionCount(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam int year,
            @RequestParam int month
    );



    @Operation(
            summary = "프로필 사진 확정",
            description = """
                    Presigned URL 업로드 후, 임시 key(tempKey)를 전달하면
                    실제 프로필 경로로 발행하고, 최종 프로필 이미지 URL을 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfirmPhotoResDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "profileUrl": "https://starlet-s3-bucket/profile/user-1.png"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 400, "message": "tempKey는 필수입니다." }
                                    """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @PostMapping("/photo/confirm")
    ResponseEntity<ConfirmPhotoResDto> confirmPhoto(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody @Valid ConfirmPhotoReqDto req
    );

    @Operation(
            summary = "닉네임 수정",
            description = """
                    사용자의 닉네임을 수정합니다.
                    - 2~6자 이내 닉네임만 허용
                    - 앞 뒤 공백 자동 제거
                    - 기존 닉네임과 동일하면 변경하지 않고 그대로 반환
                    - 중복, 부적절한 닉네임 불가
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateNicknameResDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nickname": "new_nickname"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "닉네임 형식/길이 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 400, "message": "닉네임은 2~6자 사이여야 합니다." }
                                    """))),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 409, "message": "이미 사용 중인 닉네임입니다." }
                                    """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                                    """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "status": 404, "message": "해당 유저를 찾을 수 없습니다." }
                                    """)))
    })
    @PatchMapping("/nickname")
    ResponseEntity<UpdateNicknameResDto> updateNickname(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody @Valid UpdateNicknameReqDto req
    );

    @Operation(
            summary = "닉네임 중복 확인",
            description = """
                새 닉네임이 사용 가능한지 확인합니다.
                본인 닉네임과 동일하면 사용 가능으로 처리됩니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능"),
            @ApiResponse(responseCode = "400", description = "닉네임 형식 오류"),
            @ApiResponse(responseCode = "409", description = "닉네임 중복"),
            @ApiResponse(responseCode = "401", description = "토큰 없음/만료"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/available")
    ResponseEntity<?> checkNickname(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String newNickname
    );

}

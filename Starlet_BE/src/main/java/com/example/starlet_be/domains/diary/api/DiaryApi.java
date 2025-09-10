package com.example.starlet_be.domains.diary.api;

import com.example.starlet_be.domains.diary.dto.reqdto.DiaryCreateReqDto;
import com.example.starlet_be.domains.diary.dto.reqdto.DiaryUpdateReqDto;
import com.example.starlet_be.domains.diary.dto.resdto.DiaryResDto;
import com.example.starlet_be.domains.diary.dto.resdto.StarMonthlyResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Calendar (Diary) API", description = "감정 일기 생성/수정/조회 및 월별 별 조회 API")
public interface DiaryApi {

    @Operation(
            summary = "감정 일기 생성",
            description = """
            사용자가 하루에 하나 감정 일기를 작성할 수 있습니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiaryResDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "date": "2025-09-09",
                      "emotion": "HAPPINESS",
                      "color": "YELLOW",
                      "factors": ["FRIEND","WORK"],
                      "content": "오늘은 소공 수업을 했는데, 너무너무 재미있었다!"
                    }
                """))),
            @ApiResponse(responseCode = "400", description = "입력 누락/형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필드 누락", value = """
                        { "emotion": "감정은 필수 입력입니다." }
                    """),
                                    @ExampleObject(name = "내용 길이 오류", value = """
                        { "content": "내용은 15자 이상 300자 이하로 입력해주세요." }
                    """)
                            })),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                """))),
            @ApiResponse(responseCode = "409", description = "동일 날짜 일기 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 409, "message": "해당 날짜에 이미 감정 일기가 존재합니다." }
                """)))
    })
    @PostMapping("/diary")
    ResponseEntity<?> createDiary(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody DiaryCreateReqDto req
    );

    /* ===================================== */

    @Operation(
            summary = "감정 일기 수정(내용만)",
            description = "요청한 날짜의 일기 내용을 수정합니다. 존재하지 않으면 404를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiaryResDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "date": "2025-09-09",
                      "emotion": "HAPPINESS",
                      "color": "YELLOW",
                      "factors": ["FRIEND","WORK"],
                      "content": "사실은 수업 오기 너무너무 귀찮았다...흑흑"
                    }
                """))),
            @ApiResponse(responseCode = "400", description = "입력 누락/형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "content": "내용은 15자 이상 300자 이하로 입력해주세요." }
                """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                """))),
            @ApiResponse(responseCode = "404", description = "해당 날짜 일기 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 404, "message": "해당 날짜의 일기를 찾을 수 없습니다. (2025-09-09)" }
                """)))
    })
    @PatchMapping("/diary")
    ResponseEntity<?> updateDiary(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody DiaryUpdateReqDto req
    );

    /* ===================================== */

    @Operation(
            summary = "특정 날짜 감정 일기 조회",
            description = "YYYY-MM-DD 형식의 날짜로 해당 날짜 일기를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiaryResDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "date": "2025-09-09",
                      "emotion": "HAPPINESS",
                      "color": "YELLOW",
                      "factors": ["FRIEND","WORK"],
                      "content": "오늘은 소공 수업을 했는데, 너무너무 재미있었다!"
                    }
                """))),
            @ApiResponse(responseCode = "400", description = "날짜 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 400, "message": "date 파라미터 형식이 올바르지 않습니다." }
                """))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                """))),
            @ApiResponse(responseCode = "404", description = "해당 날짜 일기 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 404, "message": "해당 날짜의 감정 일기를 찾을 수 없습니다." }
                """)))
    })
    @GetMapping("/diary/{date}")
    ResponseEntity<?> getDiary(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    /* ===================================== */

    @Operation(
            summary = "월별 별 목록 조회",
            description = """
            year, month 쿼리 파라미터로 해당 월의 별(하이라이트) 목록을 반환합니다.
            예: GET /api/v1/calendar/star?year=2025&month=9
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StarMonthlyResDto.class),
                            examples = @ExampleObject(value = """
                    [
                      { "date": "2025-09-03", "color": "ORANGE" },
                      { "date": "2025-09-12", "color": "YELLOW" },
                      { "date": "2025-09-21", "color": "BLUE" }
                    ]
                """))),
            @ApiResponse(responseCode = "400", description = "파라미터 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "year 누락", value = """
                        { "status": 400, "message": "year 파라미터는 필수입니다." }
                    """),
                                    @ExampleObject(name = "month 누락", value = """
                        { "status" : 400, "message": "month 파라미터는 필수입니다." }
                    """),
                                    @ExampleObject(name = "year 형식 오류", value = """
                        { "status" : 400, "message": "year 파라미터 형식이 올바르지 않습니다." }
                    """),
                                    @ExampleObject(name = "month 형식 오류", value = """
                        { "status" : 400, "message": "month 파라미터 형식이 올바르지 않습니다." }
                    """),
                                    @ExampleObject(name = "month 범위 오류", value = """
                        { "status": 400, "message": "month는 1~12 사이여야 합니다." }
                    """)
                            })),

            @ApiResponse(responseCode = "401", description = "액세스 토큰 미입력/만료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    { "status": 401, "message": "토큰이 없거나 만료되었습니다." }
                """)))
    })
    @GetMapping("/star")
    ResponseEntity<List<StarMonthlyResDto>> getMonthlyStars(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam int year,
            @RequestParam int month
    );
}

package com.example.starlet_be.domains.star.controller;


import com.example.starlet_be.domains.star.reqdto.StarPositionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

@Tag(name = "Star API", description = "별 관련 CRU가 구현되어 있고, 별을 렌더링하기 위한 필수 API입니다.")
public interface StarApi {

    @Operation(summary = "별 정보 조회", description = "별과 그의 연관관계들의 id를 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "starId" : "<starId>",
                                        "userId" : "<userId>",
                                        "diaryId" : "<diaryId>"
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 별",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별을 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getStar(@PathVariable Long id);



    @Operation(summary = "특정 날짜 기간 별들 조회",
            description = "YYYY-MM-DD 형식의 날짜로 두 달씩 묶은 단위로 별들을 조회합니다. 예) 2025-09-13 -> 9~10월 조회, 2025-12-13 -> 11~12월 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "starId" : "<starId>",
                                            "userId" : "<userId>",
                                            "color" : "BLUE",
                                            "date" : "2025-09-13",
                                            "x" : "<x>",
                                            "y" : "<y>"
                                        },
                                        {
                                            "starId" : "<starId>",
                                            "userId" : "<userId>",
                                            "color" : "YELLOW",
                                            "date" : "2025-10-29",
                                            "x" : "<x>",
                                            "y" : "<y>"
                                        }
                                    ]
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "날짜 오기입",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "date 파라미터 형식이 올바르지 않습니다."
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
                    }))
    })
    ResponseEntity<?> getStarryNightStar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );



    @Operation(summary = "별 위치 최신화",
            description = "밤하늘에서 별을 이동할때 최신화하는 api입니다. 좌표의 범위는 0이상 1이하의 실수값입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최신화 성공"),
            @ApiResponse(responseCode = "400", description = "좌표 범위 벗어남",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "입력된 좌표가 범위 밖입니다."
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 별",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별을 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> repositionStar(@PathVariable Long id, @RequestBody StarPositionDto dto);

}

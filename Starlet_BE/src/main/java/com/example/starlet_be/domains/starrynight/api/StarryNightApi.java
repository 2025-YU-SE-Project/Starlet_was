package com.example.starlet_be.domains.starrynight.api;

import com.example.starlet_be.domains.star.dto.request.StarPositionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Starry Night API", description = "밤하늘 페이지 관련 API 입니다.")
public interface StarryNightApi {

    @Operation(summary = "밤하늘 별 조회",
            description = "두 달씩 묶은 단위로 별들을 조회합니다. 예) 2025년 9월 -> 9~10월 조회, 2025년 12월 -> 11~12월 조회")
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
            @ApiResponse(responseCode = "400", description = "월 오기입",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "month는 1~12 사이여야 합니다."
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
            @RequestParam int year,
            @RequestParam int month
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

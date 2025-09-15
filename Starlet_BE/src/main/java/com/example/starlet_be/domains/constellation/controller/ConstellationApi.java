package com.example.starlet_be.domains.constellation.controller;

import com.example.starlet_be.domains.constellation.reqdto.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.reqdto.CreateConstellationDto;
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

@Tag(name = "Constellation API", description = "별자리 관련 API 입니다.")
public interface ConstellationApi {

    @Operation(summary = "밤하늘 별자리 조회",
            description = "두 달씩 묶은 단위로 별자리들을 조회합니다. 예) 2025년 9월 -> 9~10월 조회, 2025년 12월 -> 11~12월 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "constellationId": 1,
                                            "userId": 1,
                                            "x": 0.1,
                                            "y": 0.2,
                                            "stars": [
                                                {
                                                    "starId": 1,
                                                    "userId": 1,
                                                    "color": "YELLOW",
                                                    "date": "2025-09-11",
                                                    "x": 0.7188774459338013,
                                                    "y": 0.4530155257719439
                                                },
                                                {
                                                    "starId": 2,
                                                    "userId": 1,
                                                    "color": "YELLOW",
                                                    "date": "2025-09-12",
                                                    "x": 0.6212809212393645,
                                                    "y": 0.8268813291716997
                                                },
                                                {
                                                    "starId": 3,
                                                    "userId": 1,
                                                    "color": "YELLOW",
                                                    "date": "2025-09-13",
                                                    "x": 0.5702006236762566,
                                                    "y": 0.861161924617033
                                                }
                                            ],
                                            "connections": [
                                                {
                                                    "connectionId": 1,
                                                    "startStarId": 1,
                                                    "endStarId": 2
                                                },
                                                {
                                                    "connectionId": 1,
                                                    "startStarId": 2,
                                                    "endStarId": 3
                                                }
                                            ]
                                        },
                                        {
                                            "constellationId": 2,
                                            "userId": 1,
                                            "x": 0.6550693249242013,
                                            "y": 0.19746977186845616,
                                            "stars": [
                                                {
                                                    "starId": 4,
                                                    "userId": 1,
                                                    "color": "YELLOW",
                                                    "date": "2025-09-15",
                                                    "x": 0.4,
                                                    "y": 0.4
                                                },
                                                {
                                                    "starId": 5,
                                                    "userId": 1,
                                                    "color": "YELLOW",
                                                    "date": "2025-09-16",
                                                    "x": 0.5,
                                                    "y": 0.5
                                                }
                                            ],
                                            "connections": [
                                                {
                                                    "connectionId": 1,
                                                    "startStarId": 4,
                                                    "endStarId": 5
                                                }
                                            ]
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
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 유저를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getStarryNightConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    );



    ResponseEntity<?> createConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConstellationDto dto
    );


    ResponseEntity<?> repositionConstellation(
            @PathVariable Long id,
            @RequestBody ConstellationPositionDto dto
    );


}

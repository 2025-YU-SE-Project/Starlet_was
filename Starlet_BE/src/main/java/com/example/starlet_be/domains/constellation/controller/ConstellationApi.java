package com.example.starlet_be.domains.constellation.controller;

import com.example.starlet_be.domains.constellation.reqdto.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.reqdto.CreateConstellationDto;
import com.example.starlet_be.domains.constellation.reqdto.UpdateConstellationInfo;
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


    @Operation(summary = "별자리 생성",
            description = "별자리를 만드는 API입니다. 아직 논의가 필요한 부분인 만큼 초안으로 작성하였습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 생성 성공"),
//            @ApiResponse(responseCode = "400", description = "좌표 범위 벗어남",
//                    content = @Content(mediaType = "application/json", examples = {
//                            @ExampleObject(value = """
//                                    {
//                                        "status": 400,
//                                        "message": "입력된 좌표가 범위 밖입니다."
//                                    }
//                                    """)
//                    })),
            @ApiResponse(responseCode = "401", description = "토큰 만료 혹은 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 401,
                                        "message": "토큰이 없거나 만료되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 정보",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "사용자 정보 없음", value = """
                                    {
                                        "status": 404,
                                        "message": "해당 유저를 찾을 수 없습니다."
                                    }
                                    """),
                            @ExampleObject(name = "별 정보 없음", value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별을 찾을 수 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 별자리에 속해있는 별",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "이미 별자리에 소속된 별이 존재합니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> createConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConstellationDto dto
    );


    @Operation(summary = "별자리 위치 최신화",
            description = "별자리 위치가 변경되었을때 요청하는 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 최신화 성공"),
            @ApiResponse(responseCode = "400", description = "좌표 오기입",
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 별자리",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별자리를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> repositionConstellation(
            @PathVariable Long id,
            @RequestBody ConstellationPositionDto dto
    );

    @Operation(summary = "별자리 아카이브 조회", description = "사용자가 생성한 모든 별자리를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 아카이브 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "constellationId": 1,
                                            "name": "카시오페이아자리",
                                            "description": "카시오페아 자리 입니다.",
                                            "date": "2025-10-03",
                                            "isRepresentative": false,
                                            "stars": [
                                                {
                                                    "starId": 1,
                                                    "x": -0.15,
                                                    "y": 0.0,
                                                    "color": "YELLOW"
                                                },
                                                {
                                                    "starId": 2,
                                                    "x": -0.05,
                                                    "y": 0.1,
                                                    "color": "YELLOW"
                                                },
                                                {
                                                    "starId": 3,
                                                    "x": 0.05,
                                                    "y": -0.1,
                                                    "color": "YELLOW"
                                                },
                                                {
                                                    "starId": 4,
                                                    "x": 0.15,
                                                    "y": 0.1,
                                                    "color": "YELLOW"
                                                },
                                                {
                                                    "starId": 5,
                                                    "x": 0.2,
                                                    "y": 0.0,
                                                    "color": "YELLOW"
                                                },
                                                {
                                                    "starId": 6,
                                                    "x": -0.1,
                                                    "y": 0.1,
                                                    "color": "YELLOW"
                                                },
                                                {
                                                    "starId": 7,
                                                    "x": 0.1,
                                                    "y": -0.1,
                                                    "color": "YELLOW"
                                                }
                                            ],
                                            "connections": [
                                                {
                                                    "startStarId": 1,
                                                    "endStarId": 6
                                                },
                                                {
                                                    "startStarId": 6,
                                                    "endStarId": 2
                                                },
                                                {
                                                    "startStarId": 2,
                                                    "endStarId": 7
                                                },
                                                {
                                                    "startStarId": 7,
                                                    "endStarId": 4
                                                }
                                            ]
                                        }
                                    ]
                                    """)
                    })
            ),
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
    ResponseEntity<?> getArchiveList(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "별자리 아카이브 상세 조회", description = "특정 별자리의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 상세 조회 성공",
                content = @Content(mediaType = "application/json", examples = {
                        @ExampleObject(value = """
                                {
                                    "constellationId": 1,
                                    "name": "카시오페이아자리",
                                    "description": "북쪽 하늘의 W자 모양 별자리입니다.",
                                    "date": "2025-10-03",
                                    "isRepresentative": false,
                                    "stars": [
                                        {
                                            "starId": 1,
                                            "x": -0.15,
                                            "y": 0.0,
                                            "color": "YELLOW"
                                        },
                                        {
                                            "starId": 2,
                                            "x": -0.05,
                                            "y": 0.1,
                                            "color": "YELLOW"
                                        },
                                        {
                                            "starId": 3,
                                            "x": 0.05,
                                            "y": -0.1,
                                            "color": "YELLOW"
                                        },
                                        {
                                            "starId": 4,
                                            "x": 0.15,
                                            "y": 0.1,
                                            "color": "YELLOW"
                                        },
                                        {
                                            "starId": 5,
                                            "x": 0.2,
                                            "y": 0.0,
                                            "color": "YELLOW"
                                        },
                                        {
                                            "starId": 6,
                                            "x": -0.1,
                                            "y": 0.1,
                                            "color": "YELLOW"
                                        },
                                        {
                                            "starId": 7,
                                            "x": 0.1,
                                            "y": -0.1,
                                            "color": "YELLOW"
                                        }
                                    ],
                                    "connections": [
                                        {
                                            "startStarId": 1,
                                            "endStarId": 6
                                        },
                                        {
                                            "startStarId": 6,
                                            "endStarId": 2
                                        },
                                        {
                                            "startStarId": 2,
                                            "endStarId": 7
                                        },
                                        {
                                            "startStarId": 7,
                                            "endStarId": 4
                                        }
                                    ],
                                    "happynessCount": 7,
                                    "funnyCount": 0,
                                    "neutralCount": 0,
                                    "surprisingCount": 0,
                                    "angerCount": 0,
                                    "sadnessCount": 0
                                }
                                """)
                })
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 별자리",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별자리를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getArchiveDetail(@PathVariable Long id);

    @Operation(summary = "별자리 정보 수정", description = "별자리의 이름과 설명을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 별자리",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별자리를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> updateConstellationInfo(@PathVariable Long id, @RequestBody UpdateConstellationInfo dto);
}

package com.example.starlet_be.domains.archive.api;

import com.example.starlet_be.domains.constellation.dto.request.UpdateConstellationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="Archive API", description = "별자리 아카이브 관련 API 입니다.")
public interface ArchiveApi {

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

    @Operation(
            summary = "별자리 아카이브 페이지 조회",
            description = """
                    사용자가 생성한 별자리를 페이지 단위로 가져옵니다.
                    쿼리 스트링으로 뒤에 페이지 번호(0번부터 시작하는거 고려)와 가져올 데이터 수를 작성해주시면 됩니다.
                    
                    예시) http://localhost:8080/api/v1/constellation/archive/paging?page=0&size=4
                    
                    - JSON 페이징 관련 핵심 반환값 설명
                    
                    totalElements: 전체 데이터 개수 (모든 페이지를 합친 총 데이터 수, 프론트엔드에서 페이지 번호 계산 시 필수)
                    
                    totalPages: 전체 페이지 개수 (totalElements / size 로 계산된 결과), 사이즈를 고정할거기에 바로 쓰셔도 됩니다.
                    
                    last: 현재 페이지가 마지막 페이지인지 여부 (true/false)
                    
                    first: 현재 페이지가 첫 번째 페이지인지 여부 (true/false)
                    
                    size: 한 페이지당 조회할 데이터 개수 (요청한 size 값)
                    
                    number: 현재 페이지 번호 (0부터 시작, ex: 0이 1페이지)
                    
                    numberOfElements: 현재 반환된 페이지에 실제로 들어있는 데이터 개수 (마지막 페이지 등에서 size보다 작을 수 있음)
                    
                    empty: 데이터가 비어있는지 여부 (content가 비었으면 true)
                    
                    그외 응답 필드들은 중요하지 않습니다.
                    
                    
                    아래 나타난 pageable 입력 JSON 형태는 무시해주세요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 아카이브 페이지 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "content": [
                                             {
                                                 "constellationId": 2,
                                                 "name": "북두칠성",
                                                 "description": "국자 모양의 유명한 별자리입니다.",
                                                 "date": "2025-10-31",
                                                 "isRepresentative": false,
                                                 "stars": [
                                                     {
                                                         "starId": 1,
                                                         "x": 0.05,
                                                         "y": 0.0,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 2,
                                                         "x": 0.15,
                                                         "y": 0.03,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 3,
                                                         "x": 0.2,
                                                         "y": -0.05,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 4,
                                                         "x": 0.15,
                                                         "y": -0.15,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 5,
                                                         "x": 0.0,
                                                         "y": -0.1,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 6,
                                                         "x": -0.1,
                                                         "y": -0.05,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 7,
                                                         "x": -0.15,
                                                         "y": 0.05,
                                                         "color": "YELLOW"
                                                     }
                                                 ],
                                                 "connections": [
                                                     {
                                                         "startStarId": 1,
                                                         "endStarId": 2
                                                     },
                                                     {
                                                         "startStarId": 2,
                                                         "endStarId": 3
                                                     },
                                                     {
                                                         "startStarId": 3,
                                                         "endStarId": 4
                                                     },
                                                     {
                                                         "startStarId": 4,
                                                         "endStarId": 5
                                                     },
                                                     {
                                                         "startStarId": 5,
                                                         "endStarId": 6
                                                     },
                                                     {
                                                         "startStarId": 6,
                                                         "endStarId": 7
                                                     }
                                                 ]
                                             },
                                             {
                                                 "constellationId": 3,
                                                 "name": "북두칠성",
                                                 "description": "국자 모양의 유명한 별자리입니다.",
                                                 "date": "2025-10-31",
                                                 "isRepresentative": true,
                                                 "stars": [
                                                     {
                                                         "starId": 15,
                                                         "x": 0.05,
                                                         "y": 0.0,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 16,
                                                         "x": 0.15,
                                                         "y": 0.03,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 17,
                                                         "x": 0.2,
                                                         "y": -0.05,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 18,
                                                         "x": 0.15,
                                                         "y": -0.15,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 19,
                                                         "x": 0.0,
                                                         "y": -0.1,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 20,
                                                         "x": -0.1,
                                                         "y": -0.05,
                                                         "color": "YELLOW"
                                                     },
                                                     {
                                                         "starId": 21,
                                                         "x": -0.15,
                                                         "y": 0.05,
                                                         "color": "YELLOW"
                                                     }
                                                 ],
                                                 "connections": [
                                                     {
                                                         "startStarId": 15,
                                                         "endStarId": 16
                                                     },
                                                     {
                                                         "startStarId": 16,
                                                         "endStarId": 17
                                                     },
                                                     {
                                                         "startStarId": 17,
                                                         "endStarId": 18
                                                     },
                                                     {
                                                         "startStarId": 18,
                                                         "endStarId": 19
                                                     },
                                                     {
                                                         "startStarId": 19,
                                                         "endStarId": 20
                                                     },
                                                     {
                                                         "startStarId": 20,
                                                         "endStarId": 21
                                                     }
                                                 ]
                                             }
                                         ],
                                         "pageable": {
                                             "pageNumber": 0,
                                             "pageSize": 2,
                                             "sort": {
                                                 "empty": true,
                                                 "sorted": false,
                                                 "unsorted": true
                                             },
                                             "offset": 0,
                                             "paged": true,
                                             "unpaged": false
                                         },
                                         "totalElements": 2,
                                         "totalPages": 1,
                                         "last": true,
                                         "size": 2,
                                         "number": 0,
                                         "sort": {
                                             "empty": true,
                                             "sorted": false,
                                             "unsorted": true
                                         },
                                         "first": true,
                                         "numberOfElements": 2,
                                         "empty": false
                                     }
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
    ResponseEntity<?> getArchivePaging(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    );

    @Operation(summary = "별자리 아카이브 상세 조회", description = "특정 별자리의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 상세 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
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
                                             "color": "YELLOW",
                                             "date": "2025-09-01"
                                         },
                                         {
                                             "starId": 2,
                                             "x": -0.05,
                                             "y": 0.1,
                                             "color": "YELLOW",
                                             "date": "2025-09-02"
                                         },
                                         {
                                             "starId": 3,
                                             "x": 0.05,
                                             "y": -0.1,
                                             "color": "YELLOW",
                                             "date": "2025-09-03"
                                         },
                                         {
                                             "starId": 4,
                                             "x": 0.15,
                                             "y": 0.1,
                                             "color": "YELLOW",
                                             "date": "2025-09-04"
                                         },
                                         {
                                             "starId": 5,
                                             "x": 0.2,
                                             "y": 0.0,
                                             "color": "YELLOW",
                                             "date": "2025-09-05"
                                         },
                                         {
                                             "starId": 6,
                                             "x": -0.1,
                                             "y": 0.1,
                                             "color": "YELLOW",
                                             "date": "2025-09-06"
                                         },
                                         {
                                             "starId": 7,
                                             "x": 0.1,
                                             "y": -0.1,
                                             "color": "YELLOW",
                                             "date": "2025-09-07"
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
    ResponseEntity<?> updateConstellationInfo(@PathVariable Long id, @RequestBody UpdateConstellationDto dto);


    @Operation(summary = "대표 별자리 설정", description = "대표 별자리를 지정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별자리 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유해적인 정보 입력",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "입력 내용에 부적절한 내용이 포함되었습니다."
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
                            @ExampleObject(name = "별자리 정보 없음", value = """
                                    {
                                        "status": 404,
                                        "message": "해당 별자리를 찾을 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> changeRepresentativeConstellation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails);
}

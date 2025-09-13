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




    ResponseEntity<?> getStarryNightStar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );




    ResponseEntity<?> repositionStar(@RequestBody StarPositionDto dto);

}

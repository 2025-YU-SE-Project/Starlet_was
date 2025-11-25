package com.example.starlet_be.domains.friend.api;

import com.example.starlet_be.domains.friend.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friend API", description = "친구 검색 / 요청 / 수락 / 거절 / 목록 / 삭제 API")
public interface FriendApi {


    @Operation(
            summary = "친구 검색",
            description = """
                    닉네임으로 사용자를 검색하고,
                    조회 대상 사용자와의 친구 관계 상태(NONE, PENDING, ACCEPTED)를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FriendSearchResDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nickname": "이나현",
                                      "profileUrl": "https://...",
                                      "status": "NONE",
                                      "remainingSeconds": null
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "자기 자신 검색 불가",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 400, "message": "자기 자신은 검색할 수 없습니다." }
                            """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/search")
    ResponseEntity<FriendSearchResDto> searchFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String searchNickname
    );



    @Operation(
            summary = "친구 요청",
            description = """
                    특정 닉네임의 사용자에게 친구 요청을 보냅니다.
                    - 요청 유효기간: 3일
                    - 이미 친구 상태면 요청 불가
                    - 대기중(PENDING) 요청이 만료되지 않았다면 요청 불가
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "message": "친구 요청을 보냈습니다." }
                            """))),
            @ApiResponse(responseCode = "400", description = "요청 불가 (자기 자신 / 진행 중인 요청)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 400, "message": "이미 처리 중인 친구 요청이 있습니다." }
                            """))),
            @ApiResponse(responseCode = "409", description = "이미 친구 관계",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 409, "message": "이미 친구 관계입니다." }
                            """))),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @PostMapping("/request")
    ResponseEntity<?> requestFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FriendReqDto dto
    );


    @Operation(
            summary = "친구 요청 수락",
            description = """
                    friendId에 해당하는 친구 요청을 수락합니다.
                    - 요청 수신자만 수락 가능
                    - PENDING 상태가 아니면 오류
                    - 유효기간 만료 시 오류
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수락 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "message": "친구 요청을 수락했습니다." }
                            """))),
            @ApiResponse(responseCode = "400", description = "만료되었거나 PENDING 상태 아님",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 400, "message": "친구 요청 유효 시간이 만료되었습니다." }
                            """))),
            @ApiResponse(responseCode = "403", description = "요청 수신자가 아님",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 403, "message": "권한이 없습니다." }
                            """))),
            @ApiResponse(responseCode = "404", description = "친구 요청 없음")
    })
    @PostMapping("/accept")
    ResponseEntity<?> acceptFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FriendAcceptReqDto dto
    );


    @Operation(
            summary = "친구 요청 거절",
            description = "PENDING 상태이며 아직 만료되지 않은 친구 요청만 거절할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거절 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "message": "친구 요청을 거절했습니다." }
                            """))),
            @ApiResponse(responseCode = "400", description = "만료되었거나 PENDING 상태 아님",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 400, "message": "친구 요청 유효 시간이 만료되었습니다." }
                            """))),
            @ApiResponse(responseCode = "403", description = "요청 수신자가 아님",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 403, "message": "권한이 없습니다." }
                            """))),
            @ApiResponse(responseCode = "404", description = "요청 없음")
    })
    @DeleteMapping("/reject")
    ResponseEntity<?> rejectFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FriendRejectReqDto dto
    );



    @Operation(
            summary = "친구 목록 조회",
            description = """
                    ACCEPTED 상태의 친구 목록을 조회합니다.
                    각각의 친구별:
                    - 총 별 개수
                    - 총 별자리 개수
                    - 레벨
                    - 프로필 이미지 URL
                    정보를 포함합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/list")
    ResponseEntity<List<FriendListItemResDto>> getMyFriends(
            @AuthenticationPrincipal UserDetails principal
    );


    @Operation(
            summary = "받은 친구 요청 목록 조회",
            description = """
                    PENDING 상태 + 아직 만료되지 않은 친구 요청 목록을 최신순으로 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/requests")
    ResponseEntity<List<FriendReqItemResDto>> getMyFriendRequest(
            @AuthenticationPrincipal UserDetails principal
    );


    @Operation(
            summary = "친구 삭제",
            description = "ACCEPTED 상태의 친구 관계만 삭제할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "message": "친구를 삭제했습니다." }
                            """))),
            @ApiResponse(responseCode = "403", description = "친구 관계의 참여자가 아님",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject("""
                                { "status": 403, "message": "권한이 없습니다." }
                            """))),
            @ApiResponse(responseCode = "404", description = "친구 관계 없음")
    })
    @DeleteMapping("/{friendId}")
    ResponseEntity<?> deleteFriend(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendId
    );
}

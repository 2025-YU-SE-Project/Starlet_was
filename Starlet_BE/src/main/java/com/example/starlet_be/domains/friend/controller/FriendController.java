package com.example.starlet_be.domains.friend.controller;

import com.example.starlet_be.domains.friend.dto.*;
import com.example.starlet_be.domains.friend.service.FriendService;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final UserRepository userRepository;
    private final FriendService friendService;

    //사용자 조회
    @GetMapping("/search")
    public ResponseEntity<FriendSearchResDto> searchFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String searchNickname

    ) {
        Long userId = resolveUserId(principal);
        FriendSearchResDto body = friendService.searchFriend(userId, searchNickname);
        return ResponseEntity.ok(body);
    }

    //친구 신청
    @PostMapping("/request")
    public ResponseEntity<?> requestFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FriendReqDto dto
    ) {
        Long userId = resolveUserId(principal);
        friendService.requestFriend(userId, dto.getReceiverNickname());
        return ResponseEntity.ok(
                Map.of("message", "친구 요청을 보냈습니다."));
    }

    //친구 수락
    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FriendAcceptReqDto dto
    ) {
        Long userId = resolveUserId(principal);
        friendService.acceptFriend(userId, dto.getFriendId());

        return ResponseEntity.ok(
                Map.of("message", "친구 요청을 수락했습니다.")
        );
    }

    //친구 거절
    @DeleteMapping("/reject")
    public ResponseEntity<?> rejectFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FriendRejectReqDto dto ) {
        Long userId = resolveUserId(principal);
        friendService.rejectFriend(userId, dto.getFriendId());

        return ResponseEntity.ok(
                Map.of("message", "친구 요청을 거절했습니다.")
        );
    }

    //친구 목록
    @GetMapping("/list")
    public ResponseEntity<List<FriendListItemResDto>> getMyFriends(
            @AuthenticationPrincipal UserDetails principal
    ) {
        Long userId = resolveUserId(principal);
        List<FriendListItemResDto> body = friendService.getMyFriends(userId);
        return ResponseEntity.ok(body);
    }

    //친구 요청 목록
    @GetMapping("/requests")
    public ResponseEntity<List<FriendReqItemResDto>> getMyFriendRequest(
            @AuthenticationPrincipal UserDetails principal
    ) {
        Long userId = resolveUserId(principal);
        List<FriendReqItemResDto> body = friendService.getMyFriendRequests(userId);
        return ResponseEntity.ok(body);
    }

    //친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendId
    ) {
        Long userId = resolveUserId(principal);
        friendService.deleteFriend(userId, friendId);

        return ResponseEntity.ok(
                Map.of("message", "친구를 삭제했습니다.")
        );
    }

    private Long resolveUserId(UserDetails principal) {
        String email = principal.getUsername();
        return userRepository.findByEmailAddress(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다: " + email));
    }

}

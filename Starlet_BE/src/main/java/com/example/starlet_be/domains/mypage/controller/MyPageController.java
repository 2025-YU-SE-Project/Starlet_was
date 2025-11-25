package com.example.starlet_be.domains.mypage.controller;

import com.example.starlet_be.domains.mypage.api.MyPageApi;
import com.example.starlet_be.domains.mypage.dto.*;
import com.example.starlet_be.domains.mypage.service.MyPageService;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController implements MyPageApi {

    private final MyPageService myPageService;
    private final UserRepository userRepository;

    // summary
    @GetMapping("/summary")
    public ResponseEntity<MyPageSummaryResDto> getSummary(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        Long userId = resolveUserId(principal);
        MyPageSummaryResDto body = myPageService.getSummary(userId, year, month);
        return ResponseEntity.ok(body);
    }

    // 프로필 조회 api
    @GetMapping("/user")
    public ResponseEntity<UserSummaryResDto> getUserSummary(@AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        UserSummaryResDto body = myPageService.getUserSummary(userId);
        return ResponseEntity.ok(body);
    }

    // 레벨 조회 api
    @GetMapping("/level")
    public ResponseEntity<LevelResDto> getLevel(@AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        LevelResDto body = myPageService.getLevel(userId);
        return ResponseEntity.ok(body);
    }

    // 대표 별자리 조회 api
    @GetMapping("/representative")
    public ResponseEntity<?> getRepresentative(@AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        var dto = myPageService.getRepresentativeConstellation(userId);
        return (dto == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }

    // 연간 별자리 통계 조회
    @GetMapping("/year")
    public ResponseEntity<List<MonthlyCountResDto>> getMonthlyCount(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) Integer year) {
        Long userId = resolveUserId(principal);
        List<MonthlyCountResDto> stats = myPageService.getMonthlyCount(userId, year);

        return ResponseEntity.ok(stats);
    }

    //월별 감정 통계 조회
    @GetMapping("/month")
    public ResponseEntity<List<EmotionCountResDto>> getEmotionCount(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam int year,
            @RequestParam int month ) {
        Long userId = resolveUserId(principal);
        List<EmotionCountResDto> body = myPageService.getEmotionCount(userId, year, month);
        return ResponseEntity.ok(body);
    }

    //프로필 사진 확정
    @PostMapping("/photo/confirm")
    public ResponseEntity<ConfirmPhotoResDto> confirmPhoto(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody @Valid ConfirmPhotoReqDto req) {
        Long userId = resolveUserId(principal);
        ConfirmPhotoResDto body = myPageService.confirmProfilePhoto(userId, req.getTempKey());
        return ResponseEntity.ok(body);
    }

    //프로필 닉네임 수정
    @PatchMapping("/nickname")
    public ResponseEntity<UpdateNicknameResDto> updateNickname(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody @Valid UpdateNicknameReqDto req
    ) {
        Long userId = resolveUserId(principal);
        UpdateNicknameResDto body = myPageService.updateNickname(userId, req.getNickname());
        return ResponseEntity.ok(body);
    }

    //닉네임 중복 확인
    @GetMapping("/available")
    public ResponseEntity<?> checkNickname(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String newNickname
    ) {
        Long userId = resolveUserId(principal);
        myPageService.checkNickname(userId, newNickname);
        return ResponseEntity.ok().build();
    }


    private Long resolveUserId(UserDetails principal) {
        String email = principal.getUsername();
        return userRepository.findByEmailAddress(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다: " + email));
    }
}

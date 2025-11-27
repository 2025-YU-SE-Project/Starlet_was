package com.example.starlet_be.domains.diary.controller;

import com.example.starlet_be.domains.diary.api.DiaryApi;
import com.example.starlet_be.domains.diary.dto.request.DiaryCreateReqDto;
import com.example.starlet_be.domains.diary.dto.request.DiaryUpdateReqDto;
import com.example.starlet_be.domains.diary.dto.response.DiaryResDto;
import com.example.starlet_be.domains.diary.dto.response.StarMonthlyResDto;
import com.example.starlet_be.domains.diary.service.DiaryService;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;
    private final UserRepository userRepository;

    @PostMapping("/diary")
    public ResponseEntity<DiaryResDto> createDiary(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody DiaryCreateReqDto req
    ) {
        Long userId = resolveUserId(principal);
        DiaryResDto body = diaryService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/diary")
    public ResponseEntity<DiaryResDto> updateDiary(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody DiaryUpdateReqDto req
    ) {
        Long userId = resolveUserId(principal);
        DiaryResDto body = diaryService.update(userId, req);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/diary/{date}")
    public ResponseEntity<DiaryResDto> getDiary(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = resolveUserId(principal);
        DiaryResDto body = diaryService.getByDate(userId, date);
        return ResponseEntity.ok(body);
    }


    @GetMapping("/star")
    public ResponseEntity<List<StarMonthlyResDto>> getMonthlyStars(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam int year,
            @RequestParam int month
    ) {
        if(month < 1 || month > 12) {
            throw new CustomException(ErrorCode.DIARY_INVALID_MONTH);
        }

        Long userId = resolveUserId(principal);
        YearMonth ym = YearMonth.of(year, month);
        List<StarMonthlyResDto> body = diaryService.getMonthlyStars(userId, ym);
        return ResponseEntity.ok(body);
    }

    private Long resolveUserId(UserDetails principal) {
        String email = principal.getUsername();
        return userRepository.findByEmailAddress(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다: " + email));
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Object> removeDiary(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("diaryId") Long diaryId ) {
        Long userId = resolveUserId(principal);

        diaryService.delete(userId, diaryId);
        return ResponseEntity.noContent().build();
    }

    // 한달 일기 종합 분석요약, 파라미터로 연월 입력
    @GetMapping("/diary/summary")
    public ResponseEntity<?> getDiaryMonthSummary(
            @AuthenticationPrincipal UserDetails details,
            @RequestParam Integer year,
            @RequestParam Integer month
    ){
        return ResponseEntity.ok().body(
                diaryService.getDiaryMonthSummary(details, year, month)
        );
    }
}

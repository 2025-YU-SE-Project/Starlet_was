package com.example.starlet_be.domains.star.controller;

import com.example.starlet_be.domains.star.reqdto.DiaryToStarReqDto;
import com.example.starlet_be.domains.star.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/star")
public class StarController {
    private final StarService starService;

    // 별 생성(수동, 일기가 써지며 바로 진행되게 검토중)
    @PostMapping
    public ResponseEntity<?> createStar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DiaryToStarReqDto dto) {
        starService.createStar(userDetails, dto);
        return ResponseEntity.ok().build();
    }

    // 별 상세조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getStar(@PathVariable Long id){
        return ResponseEntity.ok().body(starService.getStar(id));
    }


    // 밤하늘 페이지 별 불러오기(2달 간격)
    @GetMapping("/{date}")
    public ResponseEntity<?> getStarryNightStar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        return ResponseEntity.ok().body(starService.getStarryNightStar(date));
    }
    

    // 별 위치 최신화




}

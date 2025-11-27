package com.example.starlet_be.domains.star.controller;

import com.example.starlet_be.domains.star.api.StarApi;
import com.example.starlet_be.domains.star.dto.request.StarPositionDto;
import com.example.starlet_be.domains.star.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/star")
public class StarController implements StarApi {
    private final StarService starService;

    // 별 상세조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getStar(@PathVariable Long id){
        return ResponseEntity.ok().body(starService.getStar(id));
    }


    // 이동 준비
    
    // 밤하늘 페이지 별 불러오기(2달 간격)
    // 별자리에 속한 별들을 제외하고 불러오도록 구현
    @GetMapping
    public ResponseEntity<?> getStarryNightStar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ){
        return ResponseEntity.ok().body(starService.getStarryNightStar(userDetails, year, month));
    }


    // 이동 준비

    // 별 위치 최신화
    @PatchMapping("/reposition/{id}")
    public ResponseEntity<?> repositionStar(@PathVariable Long id, @RequestBody StarPositionDto dto){
        starService.repositionStar(id, dto);
        return ResponseEntity.ok().build();
    }




}

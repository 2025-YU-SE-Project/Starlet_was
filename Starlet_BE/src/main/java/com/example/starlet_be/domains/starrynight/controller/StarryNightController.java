package com.example.starlet_be.domains.starrynight.controller;

import com.example.starlet_be.domains.star.dto.request.StarPositionDto;
import com.example.starlet_be.domains.star.service.StarService;
import com.example.starlet_be.domains.starrynight.api.StarryNightApi;
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
@RequestMapping("/api/v1/starrynight")
public class StarryNightController implements StarryNightApi {

    private final StarService starService;

    // 밤하늘 페이지 별 불러오기(2달 간격)
    // 별자리에 속한 별들을 제외하고 불러오도록 구현
    @GetMapping("/star")
    public ResponseEntity<?> getStarryNightStar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ){
        return ResponseEntity.ok().body(starService.getStarryNightStar(userDetails, year, month));
    }


    // 별 위치 최신화
    @PatchMapping("/star/reposition/{id}")
    public ResponseEntity<?> repositionStar(@PathVariable Long id, @RequestBody StarPositionDto dto){
        starService.repositionStar(id, dto);
        return ResponseEntity.ok().build();
    }
}

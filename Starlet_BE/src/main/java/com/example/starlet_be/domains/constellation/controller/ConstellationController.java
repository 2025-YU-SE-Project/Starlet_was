package com.example.starlet_be.domains.constellation.controller;

import com.example.starlet_be.domains.constellation.reqdto.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.reqdto.CreateConstellationDto;
import com.example.starlet_be.domains.constellation.service.ConstellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/constellation")
public class ConstellationController {
    private final ConstellationService constellationService;


    // 1. 밤하늘 페이지 별자리 조회
    @GetMapping
    public ResponseEntity<?> getStarryNightConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ){
        return ResponseEntity.ok().body(constellationService.getStarryNightConstellation(userDetails, year, month));
    }


    // 2. 별자리 아카이브 조회(별자리 전체조회)



    // 3. 별자리 상세조회(별자리 및 연관관계 상세정보 조회 - 프론트엔드 요구에 맞게 담아주면 됩니다)



    // 4. 별자리 생성
    @PostMapping
    public ResponseEntity<?> createConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConstellationDto dto
    ){
        constellationService.createConstellation(userDetails, dto);
        return ResponseEntity.ok().build();
    }


    // 5. 별자리 이름 수정



    // 6. 별자리 위치 최신화
    @PatchMapping("/reposition/{id}")
    public ResponseEntity<?> repositionConstellation(@PathVariable Long id, @RequestBody ConstellationPositionDto dto){
        constellationService.repositionConstellation(id, dto);
        return ResponseEntity.ok().build();
    }






}

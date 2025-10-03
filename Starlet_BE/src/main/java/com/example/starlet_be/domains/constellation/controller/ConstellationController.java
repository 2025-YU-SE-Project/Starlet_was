package com.example.starlet_be.domains.constellation.controller;

import com.example.starlet_be.domains.constellation.reqdto.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.reqdto.CreateConstellationDto;
import com.example.starlet_be.domains.constellation.service.ConstellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/constellation")
public class ConstellationController implements ConstellationApi {
    private final ConstellationService constellationService;


    // 밤하늘 별자리

    // 1. 별자리 조회
    @GetMapping
    public ResponseEntity<?> getStarryNightConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ){
        return ResponseEntity.ok().body(constellationService.getStarryNightConstellation(userDetails, year, month));
    }

    // 2. 별자리 생성
    @PostMapping
    public ResponseEntity<?> createConstellation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConstellationDto dto
    ){
        constellationService.createConstellation(userDetails, dto);
        return ResponseEntity.ok().build();
    }

    // 3. 별자리 위치 최신화
    @PatchMapping("/reposition/{id}")
    public ResponseEntity<?> repositionConstellation(
            @PathVariable Long id,
            @RequestBody ConstellationPositionDto dto
    ){
        constellationService.repositionConstellation(id, dto);
        return ResponseEntity.ok().build();
    }



    // 별자리 아카이브

    // 1. 별자리 아카이브 조회(별자리 전체조회)
    @GetMapping("/archive")
    public ResponseEntity<?> getArchiveList(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(constellationService.getArchiveList(userDetails));
    }


    // 2. 별자리 아카이브 상세조회
    @GetMapping("/archive/{id}")
    public ResponseEntity<?> getArchiveDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id){
        return ResponseEntity.ok("sd");
    }



    // 3. 별자리 이름 수정










}

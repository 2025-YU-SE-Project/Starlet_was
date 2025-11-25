package com.example.starlet_be.domains.constellation.controller;

import com.example.starlet_be.domains.constellation.api.ConstellationApi;
import com.example.starlet_be.domains.constellation.dto.request.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.dto.request.CreateConstellationDto;
import com.example.starlet_be.domains.constellation.dto.request.UpdateConstellationDto;
import com.example.starlet_be.domains.constellation.service.ConstellationService;
import com.example.starlet_be.domains.star.dto.request.StarsIdDto;
import jakarta.validation.Valid;
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
            @Valid @RequestBody CreateConstellationDto dto
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

    // 4. 별자리 이름 추천받기
    @PostMapping("/suggest")
    public ResponseEntity<?> suggestConstellationName(
            @RequestBody StarsIdDto dto
    ){
        return ResponseEntity.ok().body(constellationService.suggestConstellationName(dto));
    }


    
    // 별자리 아카이브

    // 1. 별자리 아카이브 조회(별자리 전체조회)
    @GetMapping("/archive")
    public ResponseEntity<?> getArchiveList(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(constellationService.getArchiveList(userDetails));
    }


    // 2. 별자리 아카이브 상세조회
    @GetMapping("/archive/{id}")
    public ResponseEntity<?> getArchiveDetail(@PathVariable Long id){
        return ResponseEntity.ok().body(constellationService.getArchiveDetail(id));
    }


    // 3. 별자리 이름 및 설명 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateConstellationInfo(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConstellationDto dto
    ){
        constellationService.updateConstellationInfo(id, dto);
        return ResponseEntity.ok().build();
    }

    // 4. 대표별자리 설정 및 해제
    @PostMapping("/archive/{id}/representative")
    public ResponseEntity<?> changeRepresentativeConstellation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        constellationService.changeRepresentativeConstellation(id, userDetails);
        return ResponseEntity.ok().build();
    }


}

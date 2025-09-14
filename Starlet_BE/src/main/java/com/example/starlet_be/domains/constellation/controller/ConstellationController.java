package com.example.starlet_be.domains.constellation.controller;

import com.example.starlet_be.domains.constellation.service.ConstellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/constellation")
public class ConstellationController {
    private final ConstellationService constellationService;


    // 1. 2달 간격 별자리들 조회(밤하늘페이지 별자리조회)



    // 2. 별자리 아카이브 조회(별자리 전체조회)



    // 3. 별자리 상세조회(별자리 및 연관관계 상세정보 조회 - 프론트엔드 요구에 맞게 담아주면 됩니다)



    // 4. 별자리 생성
    @PostMapping
    public ResponseEntity<?> createConstellation(){

        return ResponseEntity.ok().build();
    }




    // 5. 별자리 이름 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateConstellationName(@PathVariable Long id){

        return ResponseEntity.ok().build();
    }


    

    // 6. 별자리 위치 최신화
    @PatchMapping("/reposition/{id}")
    public ResponseEntity<?> repositionConstellation(@PathVariable Long id){

        return ResponseEntity.ok().build();
    }






}

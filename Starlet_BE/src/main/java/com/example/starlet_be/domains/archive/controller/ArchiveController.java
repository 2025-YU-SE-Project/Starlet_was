package com.example.starlet_be.domains.archive.controller;

import com.example.starlet_be.domains.archive.api.ArchiveApi;
import com.example.starlet_be.domains.constellation.dto.request.UpdateConstellationDto;
import com.example.starlet_be.domains.constellation.service.ConstellationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/constellation")
public class ArchiveController implements ArchiveApi {

    private final ConstellationService constellationService;

    // 별자리 아카이브

    // 1. 별자리 아카이브 조회(별자리 전체조회)
    @GetMapping("/archive")
    public ResponseEntity<?> getArchiveList(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(constellationService.getArchiveList(userDetails));
    }

    // 1.1. 별자리 아카이브 조회(별자리 페이징 조회)
    @GetMapping("/archive/paging")
    public ResponseEntity<?> getArchivePaging(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ){
        return ResponseEntity.ok().body(constellationService.getArchivePaging(userDetails, pageable));
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

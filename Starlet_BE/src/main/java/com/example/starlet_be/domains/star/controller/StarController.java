package com.example.starlet_be.domains.star.controller;

import com.example.starlet_be.domains.star.reqdto.DiaryToStarReqDto;
import com.example.starlet_be.domains.star.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/star")
public class StarController {
    private final StarService starService;

    @PostMapping
    public ResponseEntity<?> createStar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DiaryToStarReqDto dto) {
        starService.createStar(userDetails, dto);
        return ResponseEntity.ok().build();
    }

}

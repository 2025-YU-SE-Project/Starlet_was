package com.example.starlet_be.openai.controller;

import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.openai.dto.ModerationDto;
import com.example.starlet_be.openai.service.ModerationService;
import com.example.starlet_be.openai.service.OpenAIBasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/openai")
public class OpenAiController {

    private final OpenAIBasicService openAIBasicService;
    private final ModerationService moderationService;

    @PostMapping
    public ResponseEntity<?> openAi(@RequestBody String prompt){
        return ResponseEntity.ok().body(openAIBasicService.getAssistance(prompt, "사용자에게 응답해주세요"));
    }

    // 1. 모더레이션 - 닉네임 및 일기 유해성 확인
    @PostMapping("/moderation")
    public ResponseEntity<?> moderation(@RequestBody String prompt){
        ModerationDto.ModerationResponse moderationResponse = moderationService.moderate(prompt);

        if(moderationResponse == null || moderationResponse.getResults() == null)
            throw new CustomException(ErrorCode.OPENAI_SERVER_ERROR);

        boolean isFlagged = moderationResponse.getResults().get(0).isFlagged();

        if(isFlagged)
            throw new CustomException(ErrorCode.INAPPROPRIATE_CONTENT);

        return ResponseEntity.ok().build();
    }


    // 별자리 이름과 설명 추천(일기의 내용, 요인, 감정 종합적 분석)

    // (어려움) 별자리 모양 추천

    // 일기 상세조회 AI의 견해 추가


    // 일기 내용을 통한 감정과 요인 추론

}

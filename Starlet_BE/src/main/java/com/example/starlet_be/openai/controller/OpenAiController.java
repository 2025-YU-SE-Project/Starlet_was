package com.example.starlet_be.openai.controller;

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

    @PostMapping("/moderation")
    public ResponseEntity<?> moderation(@RequestBody String prompt){
        ModerationDto.ModerationResponse moderationResponse = moderationService.moderate(prompt);

        if(moderationResponse == null || moderationResponse.getResults() == null)
            return ResponseEntity.badRequest().build();

        boolean isFlagged = moderationResponse.getResults().get(0).isFlagged();

        return ResponseEntity.ok().body(isFlagged);
    }

}

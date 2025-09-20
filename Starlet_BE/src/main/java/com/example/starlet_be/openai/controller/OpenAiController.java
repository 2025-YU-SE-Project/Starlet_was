package com.example.starlet_be.openai.controller;

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

    @PostMapping
    public ResponseEntity<?> openAi(@RequestBody String prompt){
        return ResponseEntity.ok().body(openAIBasicService.getAssistance(prompt, "사용자에게 응답해주세요"));
    }

}

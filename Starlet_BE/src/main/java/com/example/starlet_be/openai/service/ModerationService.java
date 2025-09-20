package com.example.starlet_be.openai.service;

import com.example.starlet_be.openai.dto.ModerationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final RestTemplate restTemplate;
    private final String moderationUrl = "https://api.openai.com/v1/moderations";


    // 텍스트를 입력 받아서 AI API에 Post요청, 결과를 응답으로 반환하는 그런 메소드
    public ModerationDto.ModerationResponse moderate(String text) {
        ModerationDto.ModerationRequest request = new ModerationDto.ModerationRequest(text);
        return restTemplate.postForObject(moderationUrl, request, ModerationDto.ModerationResponse.class);
    }
}

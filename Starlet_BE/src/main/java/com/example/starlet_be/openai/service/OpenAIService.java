package com.example.starlet_be.openai.service;


import com.example.starlet_be.openai.dto.OpenAiReqDto;
import com.example.starlet_be.openai.dto.OpenAiResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.key}")
    private String key;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

//    public String getAssistance(String userPrompt, String systemPrompt) throws JsonProcessingException
    public String getAssistance(String userPrompt, String systemPrompt) {

        // 1. 메시지 리스트 생성 및 프롬프트 추가
        List<OpenAiReqDto.Message> messages = new ArrayList<>();
        messages.add(new OpenAiReqDto.Message("system", systemPrompt));
        messages.add(new OpenAiReqDto.Message("user", userPrompt));

        // 2. 요청 객체 생성
        OpenAiReqDto req = new OpenAiReqDto("gpt-4.1-mini", messages);

        // 3. HTTP 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(key);

        // 4. HTTP 요청 생성
        HttpEntity<OpenAiReqDto> httpEntity = new HttpEntity<>(req, headers);

        // 5. API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OpenAiResDto> res = restTemplate.exchange(
                API_URL, HttpMethod.POST, httpEntity, OpenAiResDto.class);

        if (res.getBody() == null || res.getBody().getChoices().isEmpty()) {
            throw new IllegalArgumentException("OpenAI 응답이 비어있습니다.");
        }

        // 포괄적으로 일단 String 형 반환으로 작성
        return res.getBody().getChoices().get(0).getMessage().getContent();
    }
}

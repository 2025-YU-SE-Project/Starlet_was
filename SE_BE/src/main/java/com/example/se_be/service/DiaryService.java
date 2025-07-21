package com.example.se_be.service;

import com.example.se_be.dto.DiaryReqDto;
import com.example.se_be.entity.Diary;
import com.example.se_be.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;


    // 마완성
    public Diary writeDiary(DiaryReqDto dto) {

        // 1. 유저가 로그인 상태 확인

        // 2. 우선 오늘 날짜로 이미 일기를 작성했는지 확인
        if(diaryRepository.existsByCreateAt(LocalDate.now()))
            return null;

        // 2. 엔티티 변환 시도
//        Diary diary = dto.toEntity();


        return null;
    }
}

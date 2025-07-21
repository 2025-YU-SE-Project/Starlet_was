package com.example.se_be.controller;

import com.example.se_be.dto.DiaryReqDto;
import com.example.se_be.entity.Diary;
import com.example.se_be.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    // 1. 클릭한 날짜의 일기 조회


    // 2. 일기 작성
    @PostMapping
    public ResponseEntity<?> writeDiary(@RequestBody DiaryReqDto dto){
        Diary diary = diaryService.writeDiary(dto);

        return (diary != null) ?
                ResponseEntity.ok().body(diary) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력에 문제가 있습니다.");
    }


    // 3. 방금 작성한 일기를 수정하기(제한시간 내에서만 가능, LocalDate 이용)


    // 4. 클릭한 날짜의 일기 삭제





}

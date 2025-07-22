package com.example.starlet_be.dto;

import com.example.starlet_be.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiaryReqDto {
    private String username;
    private String emotion;
    private List<String> factors;
    private String content;
    private String createAt;

}

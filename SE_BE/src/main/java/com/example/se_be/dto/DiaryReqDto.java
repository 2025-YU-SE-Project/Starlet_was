package com.example.se_be.dto;

import com.example.se_be.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiaryReqDto {
    private Long id;
    private String username;
    private String emotion;
    private List<String> factors;
    private String content;
    private String createAt;

}

package com.example.starlet_be.domains.user.reqdto;

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

package com.example.starlet_be.domains.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmPhotoReqDto {
    @NotBlank
    private String tempKey;
}

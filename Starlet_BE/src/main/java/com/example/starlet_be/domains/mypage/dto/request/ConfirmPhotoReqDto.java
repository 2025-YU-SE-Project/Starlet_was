package com.example.starlet_be.domains.mypage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmPhotoReqDto {
    @NotBlank
    @Schema(
            description = "임시 S3 객체 key. 기본 이미지로 변경하려면 'defaults' 전달",
            example = "uploads/users/1/profile1.png"
    )
    private String tempKey;
}

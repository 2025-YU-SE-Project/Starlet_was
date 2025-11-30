package com.example.starlet_be.domains.mypage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateNicknameReqDto {
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 2, max = 6, message = "닉네임은 2~6자여야 합니다.")
    @Schema(example = "새닉네임")
    private String nickname;
}
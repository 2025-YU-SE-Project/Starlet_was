package com.example.starlet_be.domains.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UpdateNicknameResDto {
    @Schema(example = "수정닉네임")
    private final String nickname;
}
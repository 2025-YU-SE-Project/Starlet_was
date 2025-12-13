package com.example.starlet_be.domains.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class UserResDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "starlet")
    private String nickname;

    @Schema(example = "starlet@example.com")
    private String email;

    @Builder public UserResDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}

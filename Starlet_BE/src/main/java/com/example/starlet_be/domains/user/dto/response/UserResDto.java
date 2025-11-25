package com.example.starlet_be.domains.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserResDto {
    private Long id;
    private String nickname;
    private String email;

    @Builder public UserResDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}

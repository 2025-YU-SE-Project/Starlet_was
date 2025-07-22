package com.example.starlet_be.dto;

import com.example.starlet_be.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserReqDto {
    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .password(password)
                .email(email)
                .build();
        // 위에 인텔리제이 자동완성인데 저건 뭔지 봐야할 듯 : 빌더의 의도에 맞게 된 문법이라 저렇게 쓰는게 맞음
//        return new User(username, password, email);
    }
}

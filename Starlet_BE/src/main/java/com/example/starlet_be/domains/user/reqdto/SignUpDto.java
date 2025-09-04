package com.example.starlet_be.domains.user.reqdto;

import com.example.starlet_be.domains.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpDto {

    @Schema(description = "사용자 닉네임", example = "우리 은하")
    @NotBlank(message = "닉네임은 필수 입력입니다.")
    private String nickname;

    @Schema(description = "비밀번호", example = "sl1234")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

    @Schema(description = "사용자 이메일", example = "starlet2025@gmail.com")
    @Email(message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .nickname(nickname)
                .password(encodedPassword)
                .email(email)
                .build();
    }
}

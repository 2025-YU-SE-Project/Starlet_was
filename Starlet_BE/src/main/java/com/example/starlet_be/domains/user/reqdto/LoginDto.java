package com.example.starlet_be.domains.user.reqdto;

import com.example.starlet_be.domains.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginDto {

    @Schema(description = "비밀번호", example = "sl1234")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

    @Schema(description = "사용자 이메일", example = "starlet2025@gmail.com")
    @Email(message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }
}

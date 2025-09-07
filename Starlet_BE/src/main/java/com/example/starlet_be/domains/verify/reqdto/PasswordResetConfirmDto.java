package com.example.starlet_be.domains.verify.reqdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    @Schema(description = "사용자 이메일", example = "starlet2025@gmail.com")
    @Email(message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "sl4321")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String newPassword;
}

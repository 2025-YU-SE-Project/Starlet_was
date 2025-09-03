package com.example.starlet_be.domains.user.reqdto;

import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    private String email;
    private String newPassword;
}

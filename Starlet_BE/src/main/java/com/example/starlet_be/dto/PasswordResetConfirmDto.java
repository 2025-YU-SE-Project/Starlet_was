package com.example.starlet_be.dto;

import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    private String email;
    private String newPassword;
}

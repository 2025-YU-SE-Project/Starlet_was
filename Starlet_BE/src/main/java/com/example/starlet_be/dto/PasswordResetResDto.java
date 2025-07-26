package com.example.starlet_be.dto;

import lombok.Data;

@Data
public class PasswordResetResDto {
    private String token;
    private String newPassword;
}

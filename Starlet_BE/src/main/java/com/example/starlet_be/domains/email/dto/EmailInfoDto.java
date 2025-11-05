package com.example.starlet_be.domains.email.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailInfoDto {
    private Long emailId;
    private String emailAddress;
    private String verifyType;
    private String verifyExpireAt;

    @Builder public EmailInfoDto(Long emailId, String emailAddress, String verifyType, String verifyExpireAt) {
        this.emailId = emailId;
        this.emailAddress = emailAddress;
        this.verifyType = verifyType;
        this.verifyExpireAt = verifyExpireAt;
    }
}

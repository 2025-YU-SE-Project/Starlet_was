package com.example.starlet_be.domains.email.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailInfoDto {

    @Schema(example = "1")
    private Long emailId;

    @Schema(example = "starlet@example.com")
    private String emailAddress;

    @Schema(example = "VERIFY")
    private String verifyType;

    @Schema(example = "null")
    private String verifyExpireAt;

    @Builder public EmailInfoDto(Long emailId, String emailAddress, String verifyType, String verifyExpireAt) {
        this.emailId = emailId;
        this.emailAddress = emailAddress;
        this.verifyType = verifyType;
        this.verifyExpireAt = verifyExpireAt;
    }
}

package com.example.starlet_be.S3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class S3uploadResDto {

    @Schema(example = "")
    private String imageUrl;

    public static S3uploadResDto of(String url) {
        return S3uploadResDto.builder()
                .imageUrl(url)
                .build();
    }
}

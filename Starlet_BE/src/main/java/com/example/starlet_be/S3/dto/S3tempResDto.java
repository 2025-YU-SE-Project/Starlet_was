package com.example.starlet_be.S3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.net.URL;

@Getter
@AllArgsConstructor
@Builder
public class S3tempResDto {

    @Schema(example = "https://starlet-s3-bucket.s3.ap-northeast-2.amazonaws.com/uploads/users/123/abc.png?...signature=...")
    private String presignedUrl;

    @Schema(example = "uploads/users/{userId}/abc.png")
    private String tempKey;

    public static S3tempResDto of(URL url, String key) {
        return S3tempResDto.builder()
                .presignedUrl(url.toString())
                .tempKey(key)
                .build();
    }

}

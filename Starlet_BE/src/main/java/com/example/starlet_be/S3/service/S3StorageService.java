package com.example.starlet_be.S3.service;

import com.example.starlet_be.S3.dto.PublishedObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Presigner presigner;
    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    @Value("${s3.region}")
    private String region;

    /**
     * 임시 저장용 사진을 위한 presignedUrl 발급
     *
     * @param key 임시 발급한 key
     * @param contentType 이미지 타입
     * @return 발급한 presigned url
     */
    public URL createUploadUrl(String key, String contentType) {
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 contentType만 허용됩니다.");
        }

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .putObjectRequest(putReq)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        return presigner.presignPutObject(presignReq).url();
    }

    /**
     * 확정된 사진의 tempkey를 받아서, 최종 사진 url을 저장
     *
     * @param userId 사용자 id
     * @param tempKey 저장할 사진의 key
     * @return 최종 사진 url
     */
    public PublishedObject publishProfile(Long userId, String tempKey) {

        String allowedPrefix = "uploads/users/" + userId + "/";
        if (tempKey == null || !tempKey.startsWith(allowedPrefix)) {
            throw new IllegalArgumentException("잘못된 tempKey입니다.");
        }

        String publicKey = "public/users/" + userId + "/profile.png";

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(tempKey)
                .destinationBucket(bucket)
                .destinationKey(publicKey)
                .build();

        s3Client.copyObject(copyReq);

        String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, publicKey);

        return PublishedObject.of(publicKey, url);
    }

    public String convertToUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, key);
    }

}

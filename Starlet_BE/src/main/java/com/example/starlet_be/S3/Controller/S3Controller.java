package com.example.starlet_be.S3.Controller;

import com.example.starlet_be.S3.dto.S3tempResDto;
import com.example.starlet_be.S3.service.S3StorageService;
import com.example.starlet_be.domains.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3StorageService s3StorageService;
    private final UserRepository userRepository;

    @PostMapping("/image/tempUrl")
    public S3tempResDto tempUrl(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String contentType
    ) {
        Long userId = resolveUserId(principal);
        String key = String.format("uploads/users/%d/%s.png", userId, UUID.randomUUID());
        URL presignedUrl = s3StorageService.createUploadUrl(key, contentType);
        return S3tempResDto.of(presignedUrl, key);
    }

    private Long resolveUserId(UserDetails principal) {
        String email = principal.getUsername();
        return userRepository.findByEmailAddress(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다: " + email));
    }
}

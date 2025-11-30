package com.example.starlet_be.domains.friend.service;

import com.example.starlet_be.domains.friend.entity.FriendStatus;
import com.example.starlet_be.domains.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FriendCleanupService {

    private final FriendRepository friendRepository;

    // 매일 새벽 3시마다
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredPendingFriends() {
        friendRepository.deleteByStatusAndExpiredAtBefore(
                FriendStatus.PENDING,
                LocalDateTime.now()
        );
    }
}

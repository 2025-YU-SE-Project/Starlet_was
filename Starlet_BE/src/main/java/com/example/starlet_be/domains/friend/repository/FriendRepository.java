package com.example.starlet_be.domains.friend.repository;

import com.example.starlet_be.domains.friend.entity.Friend;
import com.example.starlet_be.domains.friend.entity.FriendStatus;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findTop1ByRequesterAndReceiverOrderByCreatedAtDesc(
            User requester,
            User receiver
    );

    default Optional<Friend> findLatestBetween(User user1, User user2) {
        Optional<Friend> req1 = findTop1ByRequesterAndReceiverOrderByCreatedAtDesc(user1, user2);
        Optional<Friend> req2 = findTop1ByRequesterAndReceiverOrderByCreatedAtDesc(user2, user1);

        if (req1.isEmpty()) return req2;
        if (req2.isEmpty()) return req1;

        return req1.get().getCreatedAt().isAfter(req2.get().getCreatedAt()) ? req1 : req2;
    }

    List<Friend> findAllByReceiverAndStatusOrderByCreatedAtDesc(
            User receiver,
            FriendStatus status
    );

    List<Friend> findAllByStatusAndRequesterOrStatusAndReceiver(
            FriendStatus status1, User requester,
            FriendStatus status2, User receiver
    );

    long countByRequesterIdAndStatus(Long requesterId, FriendStatus status);

    long countByReceiverIdAndStatus(Long receiverId, FriendStatus status);

    default long countAcceptedFriendsByUserId(Long userId) {
        return countByRequesterIdAndStatus(userId, FriendStatus.ACCEPTED)
                + countByReceiverIdAndStatus(userId, FriendStatus.ACCEPTED);
    }

    void deleteByStatusAndExpiredAtBefore(FriendStatus status, LocalDateTime now);
}


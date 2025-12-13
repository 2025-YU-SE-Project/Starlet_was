package com.example.starlet_be.domains.friend.entity;

import com.example.starlet_be.domains.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    FriendStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;


    public static Friend createPending(User requester, User receiver, LocalDateTime expiredAt) {
        Friend friend = new Friend();
        friend.requester = requester;
        friend.receiver = receiver;
        friend.status = FriendStatus.PENDING;
        friend.createdAt = LocalDateTime.now();
        friend.updatedAt = null;
        friend.expiredAt = expiredAt;
        return friend;
    }

    //친구 요청 수락
    public void accept() {
        this.status = FriendStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
        this.expiredAt = null;
    }

    //유효 여부
    public boolean isPendingAndNotExpired() {
        return this.status == FriendStatus.PENDING
                && this.expiredAt != null
                && this.expiredAt.isAfter(LocalDateTime.now());
    }

    //남은 유효시간
    public Long getRemainingSeconds() {
        if (expiredAt == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (!expiredAt.isAfter(now)) return null;
        return java.time.Duration.between(now, expiredAt).getSeconds();
    }

}

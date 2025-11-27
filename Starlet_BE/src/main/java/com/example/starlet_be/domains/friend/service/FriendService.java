package com.example.starlet_be.domains.friend.service;

import com.example.starlet_be.S3.service.S3StorageService;
import com.example.starlet_be.domains.constellation.repository.ConstellationRepository;
import com.example.starlet_be.domains.friend.dto.response.FriendListItemResDto;
import com.example.starlet_be.domains.friend.dto.request.FriendReqItemResDto;
import com.example.starlet_be.domains.friend.dto.response.FriendSearchResDto;
import com.example.starlet_be.domains.friend.entity.Friend;
import com.example.starlet_be.domains.friend.entity.FriendStatus;
import com.example.starlet_be.domains.friend.repository.FriendRepository;
import com.example.starlet_be.domains.mypage.dto.response.LevelResDto;
import com.example.starlet_be.domains.mypage.level.LevelPolicy;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendEmailService friendEmailService;
    private final S3StorageService s3StorageService;

    private final StarRepository starRepository;
    private final ConstellationRepository constellationRepository;

    /**
     * 닉네임을 기준으로 사용자를 검색하고,
     * 나와의 친구 관계 상태(NONE / PENDING / ACCEPTED)를 함께 반환
     *
     * @param userId   검색을 수행하는 사용자 ID
     * @param nickname 검색할 대상 사용자의 닉네임
     * @return 대상 유저 정보 및 친구 상태가 포함된 응답 DTO
     * @throws CustomException USER_NOT_FOUND, CANNOT_SEARCH_SELF
     */
    @Transactional(readOnly = true)
    public FriendSearchResDto searchFriend(Long userId, String nickname) {
        User me = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        User target = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if(me.getId().equals(target.getId())) {
            throw new CustomException(ErrorCode.CANNOT_SEARCH_SELF);
        }

        var latest = friendRepository.findLatestBetween(me, target);

        String profileUrl = s3StorageService.convertToUrl(target.getProfilePhotoUrl());

        //관계 없음
        if(latest.isEmpty()) {
            return FriendSearchResDto.of(
                    target.getNickname(),
                    profileUrl,
                    "NONE",
                    null
            );
        }

        Friend f = latest.get();

        //이미 친구 상태
        if(f.getStatus() == FriendStatus.ACCEPTED) {
            return FriendSearchResDto.of(
                    target.getNickname(),
                    profileUrl,
                    "ACCEPTED",
                    null
            );
        }

        //PENDING상태 + 유효시간
        if (f.isPendingAndNotExpired()) {
            return FriendSearchResDto.of(
                    target.getNickname(),
                    profileUrl,
                    "PENDING",
                    f.getRemainingSeconds()
            );
        }

        //PENDING 유효 만료 후 NONE
        return FriendSearchResDto.of(
                target.getNickname(),
                profileUrl,
                "NONE",
                null
        );
    }

    /**
     * 친구 요청
     * 이미 친구인 경우 예외
     * 대기 중(PENDING)이며 아직 만료되지 않은 요청이 있으면 예외
     * 요청 유효기간은 3일로 설정함
     *
     * @param requesterId      친구 요청을 보내는 사용자 ID
     * @param receiverNickname 친구 요청을 받을 사용자의 닉네임
     * @throws CustomException USER_NOT_FOUND, CANNOT_REQUEST_SELF,
     *                         FRIEND_ALREADY_EXIST, FRIEND_REQUEST_ALREADY_PENDING
     */
    @Transactional
    public void requestFriend(Long requesterId, String receiverNickname) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        User receiver = userRepository.findByNickname(receiverNickname).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (requester.getId().equals(receiver.getId())) {
            throw new CustomException(ErrorCode.CANNOT_REQUEST_SELF);
        }

        Optional<Friend> latestOpt = friendRepository.findLatestBetween(requester, receiver);

        if(latestOpt.isPresent()) {
            Friend latest = latestOpt.get();

            //이미 친구
            if (latest.getStatus() == FriendStatus.ACCEPTED) {
                throw new CustomException(ErrorCode.FRIEND_ALREADY_EXIST);
            }

            //유효시간 만료 전
            if (latest.isPendingAndNotExpired()) {
                throw new CustomException(ErrorCode.FRIEND_REQUEST_ALREADY_PENDING);
            }
        }

        //유효기간 : 3일
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(3);
        Friend newRequest = Friend.createPending(requester, receiver, expiredAt);
        friendRepository.save(newRequest);

        friendEmailService.sendFriendRequestMail(requester, receiver, newRequest);
    }

    /**
     * 친구 요청 수락
     *
     * 해당 요청의 수신자와 현재 사용자 일치 여부 검증
     * PENDING 상태 여부 검증
     * 요청의 유효기간(pending + not expired) 검증
     *
     * @param receiverId 친구 요청을 수락하는 사용자 ID
     * @param friendId   수락할 Friend 엔티티 ID
     * @throws CustomException USER_NOT_FOUND, FRIEND_REQUEST_NOT_FOUND,
     *                         FORBIDDEN, FRIEND_REQUEST_EXPIRED
     */
    @Transactional
    public void acceptFriend(Long receiverId, Long friendId) {
        User receiver = userRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Friend request = friendRepository.findById(friendId).orElseThrow(
                () -> new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND)
        );

        //본인 확인
        if (!request.getReceiver().getId().equals(receiver.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        //PENDING 상태가 아닐 경우
        if (request.getStatus() != FriendStatus.PENDING) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        //유효기간 지남
        if (!request.isPendingAndNotExpired()) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_EXPIRED);
        }

        request.accept();

        friendEmailService.sendFriendAcceptedMail(request.getRequester(), receiver);
    }

    /**
     * 친구 요청 거절(삭제)
     *
     * @param receiverId 친구 요청을 거절하는 사용자 ID
     * @param friendId   거절할 Friend 엔티티 ID
     * @throws CustomException USER_NOT_FOUND, FRIEND_REQUEST_NOT_FOUND,
     *                         FORBIDDEN, FRIEND_REQUEST_EXPIRED
     */
    @Transactional
    public void rejectFriend(Long receiverId, Long friendId) {
        User receiver = userRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Friend request = friendRepository.findById(friendId).orElseThrow(
                () -> new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND)
        );

        if(!request.getReceiver().getId().equals(receiver.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if(request.getStatus() != FriendStatus.PENDING) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        if(!request.isPendingAndNotExpired()) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_EXPIRED);
        }

        friendRepository.delete(request);
    }

    /**
     * 받은 친구 요청 목록 조회
     *
     * @param userId 사용자 ID
     * @return 친구 요청 목록 응답 DTO 리스트
     * @throws CustomException USER_NOT_FOUND
     */
    @Transactional(readOnly = true)
    public List<FriendReqItemResDto> getMyFriendRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        List<Friend> requests = friendRepository
                .findAllByReceiverAndStatusOrderByCreatedAtDesc(user, FriendStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();

        return requests.stream()
                .filter(Friend::isPendingAndNotExpired)
                .map(f -> {
                    Long remainingSeconds = f.getRemainingSeconds();
                    String dDayLabel = toDDayLabel(now, f.getExpiredAt());

                    String profileUrl = s3StorageService
                            .convertToUrl(f.getRequester().getProfilePhotoUrl());

                    return FriendReqItemResDto.of(
                            f.getId(),
                            f.getRequester().getNickname(),
                            profileUrl,
                            remainingSeconds,
                            dDayLabel
                    );
                })
                .toList();
    }

    /**
     * 친구(ACCEPTED) 목록 조회
     * 각 친구별로 보유 별/별자리 수와 레벨 정보를 함께 계산해서 내려줌
     *
     * @param userId 현재 로그인한 사용자 ID
     * @return 친구 목록 응답 DTO 리스트
     * @throws CustomException USER_NOT_FOUND
     */
    @Transactional(readOnly = true)
    public List<FriendListItemResDto> getMyFriends(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        List<Friend> relations = friendRepository.findAllByStatusAndRequesterOrStatusAndReceiver(FriendStatus.ACCEPTED, user, FriendStatus.ACCEPTED, user);

        return relations.stream()
                .map(f -> {
                    User friend = f.getRequester().getId().equals(userId)
                            ? f.getReceiver() : f.getRequester();

                    long totalStars = starRepository.countByUser(friend);
                    long totalConstellations = constellationRepository.countByUser(friend);

                    LevelResDto levelBody = LevelPolicy.resolve(totalStars);
                    String level = levelBody.getName();

                    String profileUrl = s3StorageService.convertToUrl(friend.getProfilePhotoUrl());

                    return FriendListItemResDto.of(
                            f.getId(),
                            friend.getId(),
                            friend.getNickname(),
                            profileUrl,
                            totalStars,
                            totalConstellations,
                            level
                    );
                })
                .toList();
    }

    /**
     * 친구 관계 삭제
     *
     * 요청자가 해당 친구 관계의 참여자인지 검증
     * 현재 상태가 ACCEPTED 인 경우에만 삭제 허용
     *
     * @param userId   사용자 ID
     * @param friendId 삭제할 Friend 엔티티 ID
     * @throws CustomException USER_NOT_FOUND, FRIEND_NOT_FOUND, FORBIDDEN
     */
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Friend relation = friendRepository.findById(friendId).orElseThrow(
                () -> new CustomException(ErrorCode.FRIEND_NOT_FOUND)
        );

        boolean isParticipant = relation.getRequester().getId().equals(user.getId()) || relation.getReceiver().getId().equals(user.getId());

        if(!isParticipant) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if(relation.getStatus() != FriendStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.FRIEND_NOT_FOUND);
        }

        friendRepository.delete(relation);
    }



    //[친구요청목록] 남은 날짜 계산
    private String toDDayLabel(LocalDateTime now, LocalDateTime expiredAt) {
        if(expiredAt == null) return null;

        LocalDate today = now.toLocalDate();
        LocalDate target = expiredAt.toLocalDate();

        long days = ChronoUnit.DAYS.between(today, target);

        if (days > 0) {
            return "D-" + days;
        } else if (days == 0) {
            return "D-DAY";
        } else {
            return "EXPIRED";
        }
    }


}

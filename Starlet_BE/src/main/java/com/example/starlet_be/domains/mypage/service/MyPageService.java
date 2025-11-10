package com.example.starlet_be.domains.mypage.service;

import com.example.starlet_be.S3.dto.PublishedObject;
import com.example.starlet_be.S3.service.S3StorageService;
import com.example.starlet_be.domains.constellation.dto.StarryNightConstellationDto;
import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.constellation.repository.ConstellationRepository;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.diary.entity.Emotion;
import com.example.starlet_be.domains.diary.repository.DiaryRepository;
import com.example.starlet_be.domains.mypage.dto.*;
import com.example.starlet_be.domains.mypage.level.LevelPolicy;
import com.example.starlet_be.domains.mypage.mapper.ConstellationMapper;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final ConstellationRepository constellationRepository;
    private final StarRepository starRepository;
    private final ConstellationMapper constellationMapper;
    private final S3StorageService s3StorageService;

    /**
     * 마이페이지 요약 정보 조회
     * <p>
     * 사용자 ID를 기반으로 프로필, 레벨, 대표 별자리,
     * 연간 별자리 통계 및 월별 감정 통계 데이터를 반환
     * <br>
     * <code>year</code>나 <code>month</code>가 null인 경우, 현재 날짜를 기준으로 조회
     * </p>
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도 (null일 경우 현재 연도)
     * @param month 조회할 달 (null일 경우 현재 달)
     * @return MyPageSummaryResDto 마이페이지 요약 정보 DTO
     *
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */

    @Transactional(readOnly = true)
    public MyPageSummaryResDto getSummary(Long userId, Integer year, Integer month) {
        LocalDate now = LocalDate.now();
        int y = (year == null ? now.getYear() : year);
        int m = (month == null ? now.getMonthValue() : month);

        UserSummaryResDto profile = getUserSummary(userId);

        LevelResDto level = getLevel(userId);

        StarryNightConstellationDto rep = getRepresentativeConstellation(userId);

        List<MonthlyCountResDto> monthlyCount = getMonthlyCount(userId, y);

        List<EmotionCountResDto> emotionCount = getEmotionCount(userId, y, m);

        return MyPageSummaryResDto.of(profile, level, rep, monthlyCount, emotionCount);
    }

    /**
     * 사용자 요약 정보 조회
     * <p>
     * 사용자 식별자로 DB에서 {@link User} 엔티티를 조회하고,
     * 해당 사용자가 보유한 전체 별 개수와 별자리 개수를 반환
     * </p>
     *
     * @param userId 사용자 ID
     * @return UserSummaryResDto 사용자 닉네임, 총 별 개수, 총 별자리 개수를 포함한 DTO
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */
    @Transactional(readOnly = true)
    public UserSummaryResDto getUserSummary(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        long totalStars = starRepository.countByUser(user);
        long totalConstellations = constellationRepository.countByUser(user);

        return UserSummaryResDto.of(user, totalStars, totalConstellations);
    }

    /**
     * 사용자 레벨 정보 조회
     * <p>
     * 사용자 ID를 기반으로 {@link User} 엔티티를 조회하고,
     * 보유한 총 별 개수를 기준으로 {@link LevelPolicy}를 통해 레벨 정보를 계산
     * </p>
     *
     * @param userId 사용자 ID
     * @return LevelResDto 사용자 레벨 및 점수 정보 DTO
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */
    @Transactional(readOnly = true)
    public LevelResDto getLevel(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Long totalStars = starRepository.countByUser(user);

        return LevelPolicy.resolve(totalStars);
    }

    /**
     * 대표 별자리 조회
     * <p>
     * 사용자 ID를 기반으로 {@link User} 엔티티를 조회하고,
     * 해당 사용자의 대표 별자리(isRepresentative=true)를 반환
     * 존재하지 않을 경우 {@code null}을 반환합니다.
     * </p>
     *
     * @param userId 사용자 ID
     * @return StarryNightConstellationDto 대표 별자리 정보 DTO (없을 경우 null)
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */
    @Transactional(readOnly = true)
    public StarryNightConstellationDto getRepresentativeConstellation(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Optional<Constellation> rep = constellationRepository.findByUserAndIsRepresentative(user, true);

        return rep.map(constellationMapper::toStarryNightDto).orElse(null);

    }

    /**
     * 연간 월별 별자리 생성 통계 조회
     * <p>
     * 지정된 연도에 대해 사용자별 별자리 생성 수를 월 단위로 그룹화하여 반환
     * </p>
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도
     * @return List&lt;MonthlyCountResDto&gt; 월별 생성된 별자리 개수 리스트
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */
    @Transactional(readOnly = true)
    public List<MonthlyCountResDto> getMonthlyCount(Long userId, int year) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Constellation> constellations =
                constellationRepository.findAllByUserAndCreateAtBetween(user, start, end);

        Map<Integer, Long> grouped = constellations.stream()
                .filter(c -> c.getCreateAt() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getCreateAt().getMonthValue(),
                        Collectors.counting()
                ));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new MonthlyCountResDto(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * 월별 감정 통계 조회
     * <p>
     * 지정된 연도와 월에 해당하는 사용자의 일기 데이터를 조회하여,
     * 감정(Enum {@link Emotion})별 일기 개수를 집계
     * </p>
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도
     * @param month 조회할 달
     * @return List&lt;EmotionCountResDto&gt; 감정별 일기 개수 리스트
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */
    @Transactional(readOnly = true)
    public List<EmotionCountResDto> getEmotionCount(Long userId, int year, int month) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Diary> diaries = diaryRepository.findAllByUserAndCreateAtBetween(user, start, end);

        Map<Emotion, Long> grouped = diaries.stream()
                .filter(d -> d.getEmotion() != null)
                .collect(Collectors.groupingBy(Diary::getEmotion, Collectors.counting()));

        return Arrays.stream(Emotion.values())
                .map(e -> new EmotionCountResDto(e.name(), grouped.getOrDefault(e, 0L)))
                .toList();
    }


    /**
     * 프로필 사진 확정(수정)
     * <p>
     * 임시 저장된 S3 객체 키(<code>tempKey</code>)를 기반으로 프로필 사진을 발행하고,
     * 사용자의 프로필 사진 URL을 업데이트
     * </p>
     *
     * @param userId 사용자 ID
     * @param tempKey S3 임시 객체 키
     * @return ConfirmPhotoResDto 변경된 프로필 사진 URL DTO
     * @throws com.example.starlet_be.exception.CustomException USER_NOT_FOUND - 존재하지 않는 사용자일 경우 발생
     */
    @Transactional
    public ConfirmPhotoResDto confirmProfilePhoto(Long userId, String tempKey) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        PublishedObject po = s3StorageService.publishProfile(user.getId(), tempKey);
        user.changeProfilePhotoUrl(po.getKey());
        return ConfirmPhotoResDto.of(po.getUrl());
    }

    /**
     * 닉네임 수정
     * <p>
     * 새로운 닉네임의 유효성을 검사하고, 중복 여부를 확인한 뒤
     * 기존 사용자 닉네임과 다를 경우 변경
     * </p>
     *
     * @param userId 사용자 ID
     * @param newNickname 새 닉네임 (2~10자 사이)
     * @return UpdateNicknameResDto 변경된 닉네임을 포함한 DTO
     *
     * @throws com.example.starlet_be.exception.CustomException
     *         <ul>
     *             <li>USER_NOT_FOUND - 존재하지 않는 사용자일 경우</li>
     *             <li>INVALID_NICKNAME - 닉네임 길이가 유효하지 않은 경우</li>
     *             <li>NICKNAME_CONFLICT - 이미 사용 중인 닉네임일 경우</li>
     *         </ul>
     */
    @Transactional
    public UpdateNicknameResDto updateNickname(Long userId, String newNickname) {
        String nickname = newNickname == null ? "" : newNickname.trim();

        if(nickname.length() < 2 || nickname.length() > 10) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME);
        }

        if(userRepository.existsByNicknameAndIdNot(nickname, userId)) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if(!nickname.equals(user.getNickname())) {
            user.changeNickname(nickname);
        }

        return UpdateNicknameResDto.of(user.getNickname());
    }

}

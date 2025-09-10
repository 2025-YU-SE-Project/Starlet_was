package com.example.starlet_be.domains.diary.service;

import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.diary.entity.Factor;
import com.example.starlet_be.domains.diary.dto.reqdto.DiaryCreateReqDto;
import com.example.starlet_be.domains.diary.dto.reqdto.DiaryUpdateReqDto;
import com.example.starlet_be.domains.diary.repository.DiaryRepository;
import com.example.starlet_be.domains.diary.dto.resdto.DiaryResDto;
import com.example.starlet_be.domains.diary.dto.resdto.StarMonthlyResDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 감정 일기(Diary) 서비스
 * - 생성 / 수정 / 조회 / 월별 별 조회
 * - 하루에 한 개의 일기만 작성 가능
 * - +(계정 탈퇴 할 거면 existsById로 확인해야하니 체크할 것)
 */
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final EntityManager em;

    /**
     * 새로운 감정 일기를 생성한다.
     *
     * date가 null이면 오늘 날짜로 기본 설정
     * 동일 사용자/날짜의 일기가 이미 있으면 DiaryExceptions.AlreadyExists 예외 발생
     *
     * @param userId 작성자 ID
     * @param req    생성 요청 DTO
     * @return DiaryResDto 응답 DTO
     *
     */
    @Transactional
    public DiaryResDto create(Long userId, DiaryCreateReqDto req) {
        LocalDate date = req.getDate();

        if (diaryRepository.existsByUser_IdAndCreateAt(userId, date)) {
            throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
        }

        User userRef = em.getReference(User.class, userId);

        Diary diary = Diary.builder()
                .user(userRef)
                .emotion(req.getEmotion())
                .factors(safeFactors(req.getFactors()))
                .content(req.getContent())
                .createAt(date)
                .build();

        diaryRepository.save(diary);

        return DiaryResDto.of(diary);
    }

    /**
     * 특정 날짜의 감정 일기를 수정한다.
     * 수정 가능한 필드: content
     *
     * @param userId 사용자 ID
     * @param req    수정 요청 DTO
     * @return DiaryResDto 응답 DTO
     * @throws CustomException ErrorCode.DIARY_NOT_FOUND 동일 사용자/날짜가 이미 존재할 경우
     */
    @Transactional
    public DiaryResDto update(Long userId, DiaryUpdateReqDto req) {
        LocalDate date = req.getDate();
        Diary diary = diaryRepository.findByUser_IdAndCreateAt(userId, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        if (req.getContent() != null) {
            diary.updateContent(req.getContent());
        }

        return DiaryResDto.of(diary);
    }

    /**
     * 특정 날짜의 감정 일기를 조회한다.
     *
     * @param userId 사용자 ID
     * @param date   조회할 날짜
     * @return DiaryResDto 응답 DTO
     * @throws CustomException ErrorCode.DIARY_NOT_FOUND 동일 사용자/날짜가 이미 존재할 경우
     */
    public DiaryResDto getByDate(Long userId, LocalDate date) {
        Diary diary = diaryRepository.findByUser_IdAndCreateAt(userId, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        return DiaryResDto.of(diary);
    }

    /**
     * 특정 월의 별들을 조회한다.
     *
     * Diary 엔티티에서 emotion → color를 계산하여 반환
     * 날짜 오름차순
     *
     * @param userId 사용자 ID
     * @param ym     조회할 년/월
     * @return StarMonthlyResDto 리스트
     */
    public List<StarMonthlyResDto> getMonthlyStars(Long userId, YearMonth ym) {
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        return diaryRepository
                .findAllByUser_IdAndCreateAtBetweenOrderByCreateAtAsc(userId, from, to)
                .stream()
                .map(d -> new StarMonthlyResDto(d.getCreateAt(), d.getEmotion().getColor()))
                .toList();
    }

    /* ======== */

    /**
     * null 안전하게 factors 리스트 생성
     */
    private List<Factor> safeFactors(List<Factor> in) {
        return (in != null) ? new ArrayList<>(in) : new ArrayList<>();
    }
}

package com.example.starlet_be.domains.diary.service;

import com.example.starlet_be.domains.diary.dto.request.DiaryCreateReqDto;
import com.example.starlet_be.domains.diary.dto.request.DiaryUpdateReqDto;
import com.example.starlet_be.domains.diary.dto.response.DiaryByDateResDto;
import com.example.starlet_be.domains.diary.dto.response.DiaryResDto;
import com.example.starlet_be.domains.diary.dto.response.DiarySummaryResDto;
import com.example.starlet_be.domains.diary.dto.response.StarMonthlyResDto;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.diary.entity.Factor;
import com.example.starlet_be.domains.diary.repository.DiaryRepository;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.openai.service.ModerationService;
import com.example.starlet_be.openai.service.OpenAIService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
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
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final EntityManager em;
    private final ModerationService moderationService;
    private final OpenAIService openAIService;

    /**
     * 새로운 감정 일기를 생성한다.
     *
     * req로 받은 date가 오늘 날짜와 다르면 DIARY_NOT_CREATE 예외 발생
     * 동일 사용자/날짜의 일기가 이미 있으면 DiaryExceptions.AlreadyExists 예외 발생
     * 일기에 부적절한 표현이 심하게 존재하면 INAPPROPRIATE_CONTENT 예외 발생
     * 감정 일기 생성 후, star 생성
     *
     * @param userId 작성자 ID
     * @param req    생성 요청 DTO
     * @return DiaryResDto 응답 DTO
     *
     */
    @Transactional
    public DiaryResDto create(Long userId, DiaryCreateReqDto req) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        LocalDate requestDate = req.getDate();
        if (requestDate != null && !requestDate.isEqual(today)) {
            throw new CustomException(ErrorCode.DIARY_NOT_CREATE);
        }

        if (diaryRepository.existsByUser_IdAndCreateAt(userId, today)) {
            throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
        }

        if (moderationService.moderate(req.getContent()).getResults().get(0).isFlagged()) {
            throw new CustomException(ErrorCode.INAPPROPRIATE_CONTENT);
        }

        try
        {
            User userRef = em.getReference(User.class, userId);

            Diary diary = Diary.builder()
                    .user(userRef)
                    .emotion(req.getEmotion())
                    .factors(safeFactors(req.getFactors()))
                    .content(req.getContent())
                    .createAt(today)
                    .build();

            diaryRepository.save(diary);

            Star star = Star.builder()
                    .color(diary.getEmotion().getColor())
                    .x((0.95 - 0.05) * Math.random() + 0.05)
                    .y((0.95 - 0.05) * Math.random() + 0.05)
                    .user(userRef)
                    .diary(diary)
                    .build();

            starRepository.save(star);


            return DiaryResDto.of(diary);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
        }

    }

    /**
     * 특정 날짜의 감정 일기를 수정한다.
     * 수정 가능한 필드: content
     * 일기에 부적절한 표현이 심하게 존재하면 INAPPROPRIATE_CONTENT 예외 발생
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
            if (moderationService.moderate(req.getContent()).getResults().get(0).isFlagged()) {
                throw new CustomException(ErrorCode.INAPPROPRIATE_CONTENT);
            }
            diary.updateContent(req.getContent());
        }

        return DiaryResDto.of(diary);
    }

    /**
     * 특정 날짜의 감정 일기를 조회한다.
     * 다이어리가 있을 경우 -> 다이어리 조회
     * 다이어리가 없을 경우 -> hasDiary=false
     *
     * @param userId 사용자 ID
     * @param date   조회할 날짜
     * @return DiaryResDto 응답 DTO
     */
    public DiaryByDateResDto getByDate(Long userId, LocalDate date) {
        return diaryRepository.findByUser_IdAndCreateAt(userId, date)
                .map(DiaryByDateResDto::of)
                .orElseGet(() -> DiaryByDateResDto.empty(date));
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

//    /**
//     * (개발용) 일기 삭제
//     *
//     * @param userId 사용자 ID
//     * @param diaryId 삭제할 diary ID
//     */
//    public void delete(Long userId, Long diaryId) {
//        Diary diary = diaryRepository.findByIdAndUser_Id(diaryId, userId)
//                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND)); // 타인/없음 은닉
//
//        diaryRepository.delete(diary);
//    }
//
    private List<Factor> safeFactors(List<Factor> in) {
        return (in != null) ? new ArrayList<>(in) : new ArrayList<>();
    }


    // 일기 요약 ai
    public DiarySummaryResDto getDiaryMonthSummary(UserDetails details, Integer year, Integer month) {
        if(month < 1 || month > 12) {
            throw new CustomException(ErrorCode.DIARY_INVALID_MONTH);
        }

        User user = userRepository.findByEmailAddress(details.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Diary> diaries = diaryRepository.findAllByUser_IdAndCreateAtBetweenOrderByCreateAtAsc(user.getId(), startDate, endDate);

        StringBuilder diarySummary = new StringBuilder();
        for (Diary diary : diaries) {
            diarySummary.append("diary createAt : " + diary.getCreateAt().toString() + ", emotion : " + diary.getEmotion() + ", factors : " + diary.getFactors() + ", content : " + diary.getContent() + "\n");
        }

        String sysPrompt = """
                이 일기 자료들은 %d년 %d월의 일기들 정보입니다.
                일기들의 정보를 확인 후 이번달의 일기 분석을 알려주세요.
                요약과 함께 위로나 조언도 꼭 부탁드립니다.
                요약이니 공백포함 200자 이내로 쓰세요.
                그리고 줄글로 쓰며 줄바꿈 문자를 굳이 넣지는 마세요.
                일기들의 내용은 다음과 같습니다.
                
                만약 넘어오는 자료가 없으면 "일기가 없습니다" 라는 문구를 반환하세요.
                """;

        String summaryResponse = openAIService.getAssistance(
                diarySummary.toString(),
                String.format(sysPrompt, year, month)
        );

        return DiarySummaryResDto.builder()
                .summary(summaryResponse)
                .build();
    }
}

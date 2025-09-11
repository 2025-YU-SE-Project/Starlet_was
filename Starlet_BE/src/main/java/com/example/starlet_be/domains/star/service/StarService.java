package com.example.starlet_be.domains.star.service;

import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.diary.repository.DiaryRepository;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.star.reqdto.DiaryToStarReqDto;
import com.example.starlet_be.domains.star.reqdto.StarPositionDto;
import com.example.starlet_be.domains.star.resdto.StarInfoDto;
import com.example.starlet_be.domains.star.resdto.StarryNightDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StarService {
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public void createStar(UserDetails userDetails, DiaryToStarReqDto dto) {
        // 1. 사용자 조회
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 날짜 파싱
        LocalDate date;
        try{
            date = LocalDate.parse(dto.getDate());
        } catch (Exception e){
            throw new CustomException(ErrorCode.DATE_PARSE_ERROR);
        }


        // 3. 해당 날짜 일기 들고오기
        Diary diary = diaryRepository.findByUser_IdAndCreateAt(user.getId(), date).orElseThrow(
                () -> new CustomException(ErrorCode.DIARY_NOT_FOUND)
        );

        // 4. 해당 사용자의 해당 일기에 별이 이미 존재하는지
        if(starRepository.existsByUserAndDiary(user, diary))
            throw new CustomException(ErrorCode.STAR_ALREADY_EXISTS);

        // 5. 별 만들기
        Star star = Star.builder()
                .color(diary.getEmotion().getColor())
                .x(Math.random())
                .y(Math.random())
                .user(user)
                .diary(diary)
                .build();

        // 6. 저장
        starRepository.save(star);

    }

    // 별 상세조회
    @Transactional(readOnly = true)
    public StarInfoDto getStar(Long id) {

        // 1. 별 정보 불러오기
        Star star = starRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
        );


        // 2. DTO에 담기
        return StarInfoDto.builder()
                .starId(star.getId())
                .userId(star.getUser().getId())
                .diaryId(star.getDiary().getId())
                .build();
    }

    // 밤하늘 페이지 별들 불러오기
    @Transactional(readOnly = true)
    public List<StarryNightDto> getStarryNightStar(LocalDate date) {
        // 시작일 정의
        int year = date.getYear();
        int month = date.getMonthValue();

        if(month % 2 == 0)
            month--;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(2).minusDays(1);

        List<Star> stars = starRepository.findByDiary_CreateAtBetween(startDate, endDate);

        List<StarryNightDto> dtos = new ArrayList<>();

        for(Star star : stars) {
            dtos.add(StarryNightDto.builder()
                            .starId(star.getId())
                            .userId(star.getUser().getId())
                            .color(star.getColor().toString())
                            .date(star.getDiary().getCreateAt().toString())
                            .x(star.getX())
                            .y(star.getY())
                            .build());
        }

        return dtos;
    }

    // 별 위치 최신화
    @Transactional
    public void repositionStar(StarPositionDto dto) {

        // 1. 별의 존재 확인
        Star star = starRepository.findById(dto.getStarId()).orElseThrow(
                () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
        );

        // 2. 좌표가 범위 안에 있는지
        if(dto.getX() < 0 || dto.getX() > 1 || dto.getY() < 0 || dto.getY() > 1)
            throw new CustomException(ErrorCode.STAR_POSITION_OUT_OF_SCOPE);

        // 3. 위치 적용
        star.setX(dto.getX());
        star.setY(dto.getY());

        // 4. 저장
        starRepository.save(star);
    }
}

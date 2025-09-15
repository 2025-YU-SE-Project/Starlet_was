package com.example.starlet_be.domains.star.service;

import com.example.starlet_be.domains.diary.repository.DiaryRepository;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.star.reqdto.StarPositionDto;
import com.example.starlet_be.domains.star.resdto.StarInfoDto;
import com.example.starlet_be.domains.star.resdto.StarryNightStarDto;
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
    public List<StarryNightStarDto> getStarryNightStar(
            UserDetails userDetails, int year, int month
    ) {
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if(month > 12 || month < 1)
            throw new CustomException(ErrorCode.DIARY_INVALID_MONTH);

        if(month % 2 == 0)
            month--;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(2).minusDays(1);

//        List<Star> stars = starRepository.findByUserAndDiary_CreateAtBetween(user, startDate, endDate);
        List<Star> stars = starRepository.findByUserAndDiary_CreateAtBetweenAndConstellationIsNull(user, startDate, endDate);

        List<StarryNightStarDto> dtos = new ArrayList<>();

        for(Star star : stars) {
            dtos.add(StarryNightStarDto.builder()
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
    public void repositionStar(Long id, StarPositionDto dto) {

        // 1. 별의 존재 확인
        Star star = starRepository.findById(id).orElseThrow(
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

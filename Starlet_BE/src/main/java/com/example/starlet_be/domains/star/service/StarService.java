package com.example.starlet_be.domains.star.service;

import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.star.dto.request.StarPositionDto;
import com.example.starlet_be.domains.star.dto.response.StarInfoDto;
import com.example.starlet_be.domains.star.dto.response.StarryNightStarDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * 별 서비스
 * 별 정보 조회, 밤하늘 별 조회, 별 위치 최신화
 *
 * 별 생성은 일기 생성에 포함되어있음
 */
@Service
@RequiredArgsConstructor
public class StarService {
    private final UserRepository userRepository;
    private final StarRepository starRepository;

    /**
     * 별 상세조회
     *
     * 아직 프론트엔드 요구사항이 반영되지 않아서 연관관계 id만 가져오게 만들었습니다.
     *
     * @param id 별ID
     * @return StarInfoDto 별, 일기, 사용자 ID 반환
     */
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

    /**
     * 밤하늘 별 불러오기
     *
     * 사용자의 별들 중 별자리에 소속되지 않고 요청한 분기에 맞는 별들을 가져옴
     *
     * 사용자가 없을 때 USER_NOT_FOUND
     * 월 입력 오류가 발생했을때 DIARY_INVALID_MONTH -> 일단 같은 뜻의 예외라서 재활용함
     *
     * @param userDetails 액세스 토큰 기반 사용자 정보
     * @param year 연도
     * @param month 월
     * @return List<StarryNightStarDto> 조회한 별 리스트
     */
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

    /**
     * 별 위치 최신화
     *
     * 밤하늘 별자리에서 별의 위치를 바꾸고 요청했을때 반영해주는 API 입니다
     *
     * id로 별을 검색했으나 별이 없다면 STAR_NOT_FOUND
     * 좌표를 범위 밖으로 입력했다면 STAR_POSITION_OUT_OF_SCOPE
     *
     * @param id 별ID
     * @param dto x,y가 담긴 위치정보
     */
    @Transactional
    public void repositionStar(Long id, StarPositionDto dto) {

        // 1. 별의 존재 확인
        Star star = starRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
        );

        // 2. 좌표가 범위 안에 있는지
        if(dto.getX() < 0 || dto.getX() > 1 || dto.getY() < 0 || dto.getY() > 1)
            throw new CustomException(ErrorCode.STAR_POSITION_OUT_OF_SCOPE);

        // 별이 너무 밖으로 나가지 않게 조정, 나간다면 제자리로
        Double changeX = (dto.getX() > 0.05 && dto.getX() < 0.95) ? star.getX() : dto.getX();
        Double changeY = (dto.getY() > 0.05 && dto.getY() < 0.95) ? star.getY() : dto.getY();

        // 3. 위치 적용
        star.changePosition(changeX, changeY);

        // 4. 저장
        starRepository.save(star);
    }
}

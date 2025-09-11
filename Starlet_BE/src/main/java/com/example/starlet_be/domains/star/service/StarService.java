package com.example.starlet_be.domains.star.service;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.diary.repository.DiaryRepository;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.star.reqdto.DiaryToStarReqDto;
import com.example.starlet_be.domains.star.resdto.StarInfoDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StarService {
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final DiaryRepository diaryRepository;

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

    public StarInfoDto getStar(Long id) {

        // 1. 별 정보 불러오기
        Star star = starRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
        );

        // 2. 별자리가 존재하는지 보기
        Long constellationId;
        if(star.getConstellation() == null)
            constellationId = null;
        else
            constellationId = star.getConstellation().getId();

        // 3. DTO에 담기
        return StarInfoDto.builder()
                .starId(star.getId())
                .userId(star.getUser().getId())
                .constellationId(constellationId)
                .diaryId(star.getDiary().getId())
                .build();
    }
}

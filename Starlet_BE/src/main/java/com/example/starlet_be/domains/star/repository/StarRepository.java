package com.example.starlet_be.domains.star.repository;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.diary.entity.Color;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    boolean existsByUserAndDiary(User user, Diary diary);

    // 사용자와 일기의 마감기한을 기준으로 가져오기
//    List<Star> findByUserAndDiary_CreateAtBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Star> findByConstellation(Constellation constellation);

    List<Star> findByUserAndDiary_CreateAtBetweenAndConstellationIsNull(User user, LocalDate startDate, LocalDate endDate);

    Integer countByConstellationAndColor(Constellation constellation, Color color);
}
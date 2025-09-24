package com.example.starlet_be.domains.diary.repository;

import com.example.starlet_be.domains.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    //1. 사용자/날짜의 일기 존재 여부
    boolean existsByUser_IdAndCreateAt(Long userId, LocalDate date);

    //2. 일기 조회
    Optional<Diary> findByUser_IdAndCreateAt(Long userId, LocalDate date);

    //3. 달력 안 별 조회
    List<Diary> findAllByUser_IdAndCreateAtBetweenOrderByCreateAtAsc(
            Long userId, LocalDate from, LocalDate to
    );

    Optional<Diary> findByIdAndUser_Id(Long diaryId, Long userId);
}

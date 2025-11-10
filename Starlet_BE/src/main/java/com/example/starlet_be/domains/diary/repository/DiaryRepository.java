package com.example.starlet_be.domains.diary.repository;

import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    boolean existsByUser_IdAndCreateAt(Long userId, LocalDate date);

    Optional<Diary> findByUser_IdAndCreateAt(Long userId, LocalDate date);

    List<Diary> findAllByUser_IdAndCreateAtBetweenOrderByCreateAtAsc(
            Long userId, LocalDate from, LocalDate to
    );

    Optional<Diary> findByIdAndUser_Id(Long diaryId, Long userId);

    List<Diary> findAllByUserAndCreateAtBetween(User user, LocalDate start, LocalDate end);

}

package com.example.starlet_be.domains.star.repository;

import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    boolean existsByUserAndDiary(User user, Diary diary);
}
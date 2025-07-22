package com.example.starlet_be.repository;

import com.example.starlet_be.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    boolean existsByCreateAt(LocalDate createAt);
}

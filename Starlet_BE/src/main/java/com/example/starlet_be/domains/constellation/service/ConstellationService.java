package com.example.starlet_be.domains.constellation.service;

import com.example.starlet_be.domains.connection.entity.Connection;
import com.example.starlet_be.domains.connection.repository.ConnectionRepository;
import com.example.starlet_be.domains.connection.reqdto.CreateConnectionDto;
import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.constellation.repository.ConstellationRepository;
import com.example.starlet_be.domains.constellation.reqdto.CreateConstellationDto;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final ConnectionRepository connectionRepository;
    private final StarRepository starRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createConstellation(UserDetails userDetails, CreateConstellationDto dto) {
        // 유저 조회
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 별자리 생성
        Constellation constellation = Constellation.builder()
                .user(user)
                .name(dto.getName())
                .description(dto.getDescription())
                .createAt(LocalDate.now())
                .isRepresentative(false)
                .x(Math.random())
                .y(Math.random())
                .build();
        constellationRepository.save(constellation);

        // 별들 저장
        for(Long starId : dto.getStars()){
            Star star = starRepository.findById(starId).orElseThrow(
                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
            );
            star.joinConstellation(constellation);
            starRepository.save(star);
        }

        // 연결 저장
        for(CreateConnectionDto con : dto.getConnections()){
            connectionRepository.save(Connection.builder()
                            .constellation(constellation)
                            .start(starRepository.findById(con.getStartStarId()).orElseThrow(
                                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
                            ))
                            .end(starRepository.findById(con.getEndStarId()).orElseThrow(
                                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
                            ))
                            .build()
            );
        }
    }
}

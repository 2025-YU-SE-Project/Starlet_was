package com.example.starlet_be.domains.constellation.service;

import com.example.starlet_be.domains.connection.entity.Connection;
import com.example.starlet_be.domains.connection.repository.ConnectionRepository;
import com.example.starlet_be.domains.connection.reqdto.CreateConnectionDto;
import com.example.starlet_be.domains.connection.resdto.StarryNightConnectionDto;
import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.constellation.repository.ConstellationRepository;
import com.example.starlet_be.domains.constellation.reqdto.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.reqdto.CreateConstellationDto;
import com.example.starlet_be.domains.constellation.resdto.StarryNightConstellationDto;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.star.reqdto.StarPositionDto;
import com.example.starlet_be.domains.star.resdto.StarryNightStarDto;
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
        for(StarPositionDto starDto : dto.getStars()){
            Star star = starRepository.findById(starDto.getStarId()).orElseThrow(
                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
            );
            if(star.getConstellation() != null)
                throw new CustomException(ErrorCode.ALREADY_BELONG_TO_CONSTELLATION);
            star.joinConstellation(constellation);
            star.changePosition(starDto.getX(), starDto.getY());
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

    @Transactional(readOnly = true)
    public List<StarryNightConstellationDto> getStarryNightConstellation(UserDetails userDetails, LocalDate date) {
        // 유저 불러오기
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 불러올 날짜 지정
        int year = date.getYear();
        int month = date.getMonthValue();


        if(month % 2 == 0)
            month--;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(2).minusDays(1);

        // 해당 분기의 별자리 불러오기
        List<Constellation> constellations =
                constellationRepository.findByUserAndCreateAtBetween(user, startDate, endDate);

        List<StarryNightConstellationDto> constellationsInfo = new ArrayList<>();

        for(Constellation con : constellations){
            // 별자리 꺼내오기

            // 별자리 기반으로 별들의 정보 꺼내오기
            List<Star> stars = starRepository.findByConstellation(con);
            List<StarryNightStarDto> starsInfo = new ArrayList<>();
            for(Star star : stars){
                starsInfo.add(StarryNightStarDto.builder()
                                .starId(star.getId())
                                .userId(star.getUser().getId())
                                .color(star.getColor().toString())
                                .date(star.getDiary().getCreateAt().toString())
                                .x(star.getX())
                                .y(star.getY())
                                .build()
                );
            }

            // 별자리 기반으로 선들의 정보 꺼내오기
            List<Connection> connections = connectionRepository.findByConstellation(con);
            List<StarryNightConnectionDto> connectionsInfo = new ArrayList<>();
            for(Connection connection : connections){
                connectionsInfo.add(StarryNightConnectionDto.builder()
                                .startStarId(connection.getStart().getId())
                                .endStarId(connection.getEnd().getId())
                                .build()
                );
            }

            // DTO에 담아 저장
            constellationsInfo.add(StarryNightConstellationDto.builder()
                            .constellationId(con.getId())
                            .userId(con.getUser().getId())
                            .x(con.getX())
                            .y(con.getY())
                            .stars(starsInfo)
                            .connections(connectionsInfo)
                            .build()
            );
        }


        return constellationsInfo;
    }

    // 별자리 위치 최신화
    @Transactional
    public void repositionConstellation(Long id, ConstellationPositionDto dto) {

        // 1. 별자리 존재 확인
        Constellation constellation = constellationRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.CONSTELLATION_NOT_FOUND)
        );

        // 2. 좌표가 범위 안인지 검사
        if(dto.getX() < 0 || dto.getX() > 1 || dto.getY() < 0 || dto.getY() > 1)
            throw new CustomException(ErrorCode.CONSTELLATION_POSITION_OUT_OF_SCOPE);

        // 3. 위치 적용
        constellation.changePosition(dto.getX(), dto.getY());

        // 4. 저장
        constellationRepository.save(constellation);

    }
}

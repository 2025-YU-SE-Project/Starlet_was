package com.example.starlet_be.domains.mypage.mapper;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.connection.dto.response.StarryNightConnectionDto;
import com.example.starlet_be.domains.star.dto.response.StarryNightStarDto;
import com.example.starlet_be.domains.constellation.dto.response.StarryNightConstellationDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ConstellationMapper {

    public StarryNightConstellationDto toStarryNightDto(Constellation c) {

        return StarryNightConstellationDto.builder()
                .constellationId(c.getId())
                .userId(c.getUser().getId())
                .x(c.getX())
                .y(c.getY())
                .belongDate(c.getBelongDate())

                .stars(c.getStars() != null
                        ? c.getStars().stream()
                        .map(s -> StarryNightStarDto.builder()
                                .starId(s.getId())
                                .userId(s.getUser().getId())
                                .color(String.valueOf(s.getColor()))
                                .x(s.getX())
                                .y(s.getY())
                                .build())
                        .collect(Collectors.toList())
                        : Collections.emptyList())

                .connections(c.getConnections() != null
                        ? c.getConnections().stream()
                        .map(conn -> StarryNightConnectionDto.builder()
                                .connectionId(conn.getId())
                                .startStarId(conn.getStart().getId())
                                .endStarId(conn.getEnd().getId())
                                .build())
                        .collect(Collectors.toList())
                        : Collections.emptyList())

                .build();
    }
}

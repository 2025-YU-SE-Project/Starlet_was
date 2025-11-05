package com.example.starlet_be.domains.constellation.dto;

import com.example.starlet_be.domains.connection.dto.ConnectionDto;
import com.example.starlet_be.domains.star.dto.StarPositionDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateConstellationDto {
    private String name;
    private String description;
    private List<StarPositionDto> stars;
    private List<ConnectionDto> connections;
}

package com.example.starlet_be.domains.constellation.reqdto;

import com.example.starlet_be.domains.connection.reqdto.ConnectionDto;
import com.example.starlet_be.domains.star.reqdto.StarPositionDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateConstellationDto {
    private String name;
    private String description;
    private List<StarPositionDto> stars;
    private List<ConnectionDto> connections;
}

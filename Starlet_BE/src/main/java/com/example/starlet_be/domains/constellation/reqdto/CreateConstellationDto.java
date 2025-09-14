package com.example.starlet_be.domains.constellation.reqdto;

import com.example.starlet_be.domains.connection.reqdto.CreateConnectionDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateConstellationDto {
    private String name;
    private String description;
    private List<Long> stars;
    private List<CreateConnectionDto> connections;
}

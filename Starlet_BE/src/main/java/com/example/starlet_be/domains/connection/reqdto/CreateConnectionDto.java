package com.example.starlet_be.domains.connection.reqdto;

import lombok.Getter;

@Getter
public class CreateConnectionDto {
    private Long startStarId;
    private Long endStarId;
}

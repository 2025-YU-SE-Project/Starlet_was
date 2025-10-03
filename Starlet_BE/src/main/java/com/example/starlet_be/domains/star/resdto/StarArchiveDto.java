package com.example.starlet_be.domains.star.resdto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StarArchiveDto {
    private Long starId;
    private String color;
}

package com.example.starlet_be.domains.star.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StarsIdDto {
    private List<Long> starIds;
}

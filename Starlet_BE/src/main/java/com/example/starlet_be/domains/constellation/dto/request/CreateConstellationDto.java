package com.example.starlet_be.domains.constellation.dto.request;

import com.example.starlet_be.domains.connection.dto.response.ConnectionDto;
import com.example.starlet_be.domains.star.dto.request.StarPositionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateConstellationDto {

    @Schema(description = "별자리 이름", example = "물병자리")
    @NotNull(message = "별자리 이름을 Null 값으로 입력할 수 없습니다.")
    @Size(max = 10, message = "별자리 이름은 10자 이내여야 합니다.")
    private String name;

    @Schema(description = "별자리 설명", example = "당신의 자리와 같은 물병자리 입니다.")
    @NotNull(message = "별자리 설명을 Null 값으로 입력할 수 없습니다.")
    @Size(max = 30, message = "별자리 설명은 30자 이내여야 합니다.")
    private String description;

    @Schema(description = "별자리로 만들 별들")
    @Size(min=7, max=14, message = "별들은 7~14개로만 구성되어야 합니다.")
    private List<StarPositionDto> stars;

    @Schema(description = "별자리로 만들 선 시작위치와 끝위치")
    private List<ConnectionDto> connections;
}

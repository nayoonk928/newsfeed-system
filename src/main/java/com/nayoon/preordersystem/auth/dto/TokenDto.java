package com.nayoon.preordersystem.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenDto(
    String accessToken,
    String refreshToken,
    Long refreshTokenExpiresTime
) {

}

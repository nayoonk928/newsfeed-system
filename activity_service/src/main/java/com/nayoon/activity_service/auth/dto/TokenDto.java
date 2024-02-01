package com.nayoon.activity_service.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenDto(
    String accessToken,
    String refreshToken,
    Long refreshTokenExpiresTime
) {

}

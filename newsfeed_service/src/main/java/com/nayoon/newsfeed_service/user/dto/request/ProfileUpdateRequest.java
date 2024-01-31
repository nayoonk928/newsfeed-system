package com.nayoon.newsfeed_service.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
    @NotBlank
    String name,
    @NotBlank
    String greeting
) {

}
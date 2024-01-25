package com.nayoon.preordersystem.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInRequest(
    @NotBlank
    String email,
    @NotBlank
    String password
) {

}

package com.nayoon.preordersystem.user.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(
    String email,
    String name,
    String profileImage,
    String greeting
) {

}

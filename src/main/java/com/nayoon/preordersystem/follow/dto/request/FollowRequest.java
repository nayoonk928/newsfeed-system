package com.nayoon.preordersystem.follow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FollowRequest(
    @NotBlank
    Long followingUserId
) {

}

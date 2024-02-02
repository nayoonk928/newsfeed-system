package com.nayoon.activity_service.follow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FollowRequest(
    @NotBlank
    Long followerUserId,
    @NotBlank
    Long followingUserId
) {

}

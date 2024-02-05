package com.nayoon.activity_service.follow.dto.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(
    @NotNull
    Long followingUserId
) {

}
